package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentifierDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.prototypes.AChildCacheableObject;
import indi.sly.system.kernel.core.prototypes.IByteValueSupporter;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.instances.prototypes.PortContentObject;
import indi.sly.system.kernel.processes.instances.prototypes.SignalContentObject;
import indi.sly.system.kernel.processes.instances.values.SignalEntryDefinition;
import indi.sly.system.kernel.processes.lang.ProcessProcessorReadComponentFunction;
import indi.sly.system.kernel.processes.lang.ProcessProcessorWriteComponentConsumer;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.*;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;
import indi.sly.system.kernel.security.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCommunicationObject extends AChildCacheableObject<ProcessChildCacheEntity, ProcessObject> implements IByteValueSupporter<ProcessCommunicationDefinition> {
    protected ProcessFactory factory;
    protected ProcessProcessorMediator processorMediator;

    private ProcessEntity getSelf() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getProcess().getProcessId())) {
            throw new ConditionContextException();
        }

        return this.processorMediator.getSelf().apply(this.cache.getProcess().getProcessId());
    }

    private ProcessCommunicationDefinition init(ProcessEntity process) {
        Set<ProcessProcessorReadComponentFunction> resolvers = this.processorMediator.getReadProcessCommunications();

        byte[] source = null;

        for (ProcessProcessorReadComponentFunction resolver : resolvers) {
            source = resolver.apply(source, process);
        }

        return IByteValueSupporter.super.init(source);
    }

    private void flush(ProcessEntity process, ProcessCommunicationDefinition value) {
        byte[] source = IByteValueSupporter.super.flush(value);

        Set<ProcessProcessorWriteComponentConsumer> resolvers = this.processorMediator.getWriteProcessCommunications();

        for (ProcessProcessorWriteComponentConsumer resolver : resolvers) {
            resolver.accept(process, source);
        }
    }

    public byte[] getShared() {
        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
        ThreadObject thread = threadManager.getCurrent();

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessCommunicationDefinition processCommunication = this.init(process);

            byte[] processCommunicationShared = processCommunication.getShared();

            ProcessStatisticsObject processStatistics = this.base.getStatistics();
            processStatistics.addSharedReadCount(1);
            processStatistics.addSharedReadBytes(processCommunicationShared.length);
            ThreadStatisticsObject threadStatistics = thread.getStatistics();
            threadStatistics.addSharedReadCount(1);
            threadStatistics.addSharedReadBytes(processCommunicationShared.length);

            return processCommunicationShared;
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public void setShared(byte[] shared) {
        if (ObjectUtil.isAnyNull(shared)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessTokenObject processToken = this.base.getToken();
        if (shared.length > processToken.getLimits().get(ProcessTokenLimitType.SHARED_LENGTH_MAX)) {
            throw new ConditionRefuseException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessCommunicationDefinition processCommunication = this.init(process);

            processCommunication.setShared(shared);

            this.flush(processCommunication);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }

        ProcessStatisticsObject processStatistics = this.base.getStatistics();
        processStatistics.addSharedWriteCount(1);
        processStatistics.addSharedWriteBytes(shared.length);
        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
        ThreadObject thread = threadManager.getCurrent();
        ThreadStatisticsObject threadStatistics = thread.getStatistics();
        threadStatistics.addSharedWriteCount(1);
        threadStatistics.addSharedWriteBytes(shared.length);
    }

    public Set<UUID> getPortIDs() {
        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessCommunicationDefinition processCommunication = this.init(process);

            return CollectionUtil.unmodifiable(processCommunication.getPortIDs());
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public UUID createPort(Set<UUID> sourceProcessIDs) {
        if (ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        KernelConfigurationDefinition kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

        PathDefinition path = new PathDefinition(List.of(new IdentifierDefinition("Ports")));

        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);

        UUID portID;

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessCommunicationDefinition processCommunication = this.init(process);

            ProcessTokenObject processToken = this.base.getToken();
            if (processCommunication.getPortIDs().size() > processToken.getLimits().get(ProcessTokenLimitType.PORT_COUNT_MAX)) {
                throw new ConditionRefuseException();
            }

            InfoObject portsInfo = objectManager.get(path);

            InfoObject portInfo = portsInfo.createChild(kernelConfiguration.PROCESSES_COMMUNICATION_INSTANCE_PORT_ID,
                    new IdentifierDefinition(UUID.randomUUID()));

            SecurityDescriptorObject securityDescriptor = portInfo.getSecurityDescriptor();
            Set<AccessControlDefinition> permissions = new HashSet<>();
            AccessControlDefinition permission = new AccessControlDefinition();
            permission.setUserId(new UserIDDefinition(this.base.getId(), UserType.PROCESS));
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(PermissionType.FULLCONTROL_ALLOW);
            permissions.add(permission);
            permission = new AccessControlDefinition();
            permission.setUserId(new UserIDDefinition(this.base.getId(), UserType.PARENT_PROCESS));
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
            permissions.add(permission);
            for (UUID sourceProcessID : sourceProcessIDs) {
                permission = new AccessControlDefinition();
                permission.setUserId(new UserIDDefinition(sourceProcessID, UserType.PROCESS));
                permission.setScope(AccessControlScopeType.THIS);
                permission.setValue(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
                permissions.add(permission);
            }
            securityDescriptor.setPermissions(permissions);

            portsInfo.open(InfoOpenAttributeType.OPEN_EXCLUSIVE);

            PortContentObject portContent = (PortContentObject) portInfo.getContent();
            portContent.setSourceProcessIDs(sourceProcessIDs);

            portsInfo.close();

            ProcessInfoTableObject processInfoTable = this.base.getInfoTable();
            ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(portInfo.getId());
            processInfoEntry.setUnsupportedDelete(true);

            portID = portInfo.getId();

            processCommunication.getPortIDs().add(portID);

            this.flush(processCommunication);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }

        ProcessStatisticsObject processStatistics = this.base.getStatistics();
        processStatistics.addPortCount(1);
        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
        ThreadObject thread = threadManager.getCurrent();
        ThreadStatisticsObject threadStatistics = thread.getStatistics();
        threadStatistics.addPortCount(1);

        return portID;
    }

    public void deleteAllPort() {
        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessCommunicationDefinition processCommunication = this.init(process);

            Set<UUID> processCommunicationPortIDs = processCommunication.getPortIDs();

            ProcessInfoTableObject processInfoTable = this.base.getInfoTable();

            for (UUID processCommunicationPortID : processCommunicationPortIDs) {
                if (processInfoTable.containById(processCommunicationPortID)) {
                    ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(processCommunicationPortID);
                    processInfoEntry.setUnsupportedDelete(false);

                    InfoObject info = processInfoEntry.getInfo();
                    info.close();
                }

                processCommunicationPortIDs.remove(processCommunicationPortID);
            }

            this.flush(processCommunication);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void deletePort(UUID portID) {
        if (ObjectUtil.isAnyNull(portID)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessCommunicationDefinition processCommunication = this.init(process);

            Set<UUID> processCommunicationPortIDs = processCommunication.getPortIDs();
            if (processCommunicationPortIDs.contains(portID)) {
                throw new StatusNotExistedException();
            }

            ProcessInfoTableObject processInfoTable = this.base.getInfoTable();
            if (processInfoTable.containById(portID)) {
                ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(portID);
                processInfoEntry.setUnsupportedDelete(false);

                InfoObject info = processInfoEntry.getInfo();
                info.close();
            }

            processCommunicationPortIDs.remove(portID);

            this.flush(processCommunication);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public Set<UUID> getPortSourceProcessIDs(UUID portID) {
        if (ObjectUtil.isAnyNull(portID)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        if (!this.getPortIDs().contains(portID)) {
            throw new StatusNotExistedException();
        }

        PathDefinition path
                = new PathDefinition(List.of(new IdentifierDefinition("Ports"), new IdentifierDefinition(portID)));

        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
        InfoObject portInfo = objectManager.get(path);

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessInfoTableObject currentProcessInfoTable = currentProcess.getInfoTable();

        boolean contain = currentProcessInfoTable.containById(portInfo.getId());
        if (!contain) {
            portInfo.open(InfoOpenAttributeType.OPEN_ONLY_READ);
        }
        PortContentObject portContent = (PortContentObject) portInfo.getContent();
        Set<UUID> sourceProcessIDs = portContent.getSourceProcessIDs();
        if (!contain) {
            portInfo.close();
        }

        return sourceProcessIDs;
    }

    public void setPortSourceProcessIDs(UUID portID, Set<UUID> sourceProcessIDs) {
        if (ValueUtil.isAnyNullOrEmpty(portID) || ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        if (!this.getPortIDs().contains(portID)) {
            throw new StatusNotExistedException();
        }

        PathDefinition path
                = new PathDefinition(List.of(new IdentifierDefinition("Ports"), new IdentifierDefinition(portID)));

        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
        InfoObject portInfo = objectManager.get(path);

        SecurityDescriptorObject securityDescriptor = portInfo.getSecurityDescriptor();
        Set<AccessControlDefinition> permissions = new HashSet<>();
        AccessControlDefinition permission = new AccessControlDefinition();
        permission.setUserId(new UserIDDefinition(this.base.getId(), UserType.PROCESS));
        permission.setScope(AccessControlScopeType.THIS);
        permission.setValue(PermissionType.FULLCONTROL_ALLOW);
        permissions.add(permission);
        permission = new AccessControlDefinition();
        permission.setUserId(new UserIDDefinition(this.base.getId(), UserType.PARENT_PROCESS));
        permission.setScope(AccessControlScopeType.THIS);
        permission.setValue(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
        permissions.add(permission);
        for (UUID sourceProcessID : sourceProcessIDs) {
            permission = new AccessControlDefinition();
            permission.setUserId(new UserIDDefinition(sourceProcessID, UserType.PROCESS));
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
            permissions.add(permission);
        }
        securityDescriptor.setPermissions(permissions);

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessInfoTableObject currentProcessInfoTable = currentProcess.getInfoTable();

        boolean contain = currentProcessInfoTable.containById(portInfo.getId());
        if (!contain) {
            portInfo.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);
        }
        PortContentObject portContent = (PortContentObject) portInfo.getContent();
        portContent.setSourceProcessIDs(sourceProcessIDs);
        if (!contain) {
            portInfo.close();
        }
    }

    public byte[] receivePort(UUID portID) {
        if (ObjectUtil.isAnyNull(portID)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        if (!this.getPortIDs().contains(portID)) {
            throw new StatusNotExistedException();
        }

        PathDefinition path
                = new PathDefinition(List.of(new IdentifierDefinition("Ports"), new IdentifierDefinition(portID)));

        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
        InfoObject portInfo = objectManager.get(path);

        PortContentObject portContent = (PortContentObject) portInfo.getContent();
        byte[] value = portContent.receive();

        ProcessStatisticsObject processStatistics = this.base.getStatistics();
        processStatistics.addPortReadCount(1);
        processStatistics.addPortReadBytes(value.length);
        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
        ThreadObject thread = threadManager.getCurrent();
        ThreadStatisticsObject threadStatistics = thread.getStatistics();
        threadStatistics.addPortReadCount(1);
        threadStatistics.addPortReadBytes(value.length);

        return value;
    }

    public void sendPort(UUID portID, byte[] value) {
        if (ValueUtil.isAnyNullOrEmpty(portID) || ObjectUtil.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        PathDefinition path
                = new PathDefinition(List.of(new IdentifierDefinition("Ports"), new IdentifierDefinition(portID)));

        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
        InfoObject portInfo = objectManager.get(path);

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessInfoTableObject currentProcessInfoTable = currentProcess.getInfoTable();

        boolean contain = currentProcessInfoTable.containById(portInfo.getId());
        if (!contain) {
            portInfo.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);
        }
        PortContentObject portContent = (PortContentObject) portInfo.getContent();
        portContent.send(value);
        if (!contain) {
            portInfo.close();
        }

        ProcessStatisticsObject processStatistics = this.base.getStatistics();
        processStatistics.addPortWriteCount(1);
        processStatistics.addPortWriteBytes(value.length);
        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
        ThreadObject thread = threadManager.getCurrent();
        ThreadStatisticsObject threadStatistics = thread.getStatistics();
        threadStatistics.addPortWriteCount(1);
        threadStatistics.addPortWriteBytes(value.length);
    }

    public UUID getSignalID() {
        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessCommunicationDefinition processCommunication = this.init(process);

            return processCommunication.getSignalID();
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public void createSignal(Set<UUID> sourceProcessIDs) {
        if (ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        KernelConfigurationDefinition kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

        PathDefinition path = new PathDefinition(List.of(new IdentifierDefinition("Signals")));

        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessCommunicationDefinition processCommunication = this.init(process);

            if (!ValueUtil.isAnyNullOrEmpty(processCommunication.getSignalID())) {
                throw new StatusAlreadyFinishedException();
            }

            InfoObject signalsInfo = objectManager.get(path);

            InfoObject signalInfo = signalsInfo.createChild(kernelConfiguration.PROCESSES_COMMUNICATION_INSTANCE_SIGNAL_ID,
                    new IdentifierDefinition(UUID.randomUUID()));

            SecurityDescriptorObject securityDescriptor = signalInfo.getSecurityDescriptor();
            Set<AccessControlDefinition> permissions = new HashSet<>();
            AccessControlDefinition permission = new AccessControlDefinition();
            permission.setUserId(new UserIDDefinition(this.base.getId(), UserType.PROCESS));
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(PermissionType.FULLCONTROL_ALLOW);
            permissions.add(permission);
            permission = new AccessControlDefinition();
            permission.setUserId(new UserIDDefinition(this.base.getId(), UserType.PARENT_PROCESS));
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
            permissions.add(permission);
            for (UUID sourceProcessID : sourceProcessIDs) {
                permission = new AccessControlDefinition();
                permission.setUserId(new UserIDDefinition(sourceProcessID, UserType.PROCESS));
                permission.setScope(AccessControlScopeType.THIS);
                permission.setValue(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
                permissions.add(permission);
            }
            securityDescriptor.setPermissions(permissions);

            signalInfo.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);

            SignalContentObject signalContent = (SignalContentObject) signalInfo.getContent();
            signalContent.setSourceProcessIDs(sourceProcessIDs);

            signalInfo.close();

            ProcessInfoTableObject processInfoTable = this.base.getInfoTable();
            ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(signalInfo.getId());
            processInfoEntry.setUnsupportedDelete(true);

            processCommunication.setSignalID(signalInfo.getId());

            this.flush(processCommunication);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void deleteSignal() {
        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessCommunicationDefinition processCommunication = this.init(process);

            UUID signalID = processCommunication.getSignalID();

            if (ValueUtil.isAnyNullOrEmpty(signalID)) {
                throw new StatusAlreadyFinishedException();
            }

            ProcessInfoTableObject processInfoTable = this.base.getInfoTable();
            if (processInfoTable.containById(signalID)) {
                ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(signalID);
                processInfoEntry.setUnsupportedDelete(false);

                InfoObject info = processInfoEntry.getInfo();
                info.close();
            }

            processCommunication.setSignalID(null);

            this.flush(processCommunication);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public Set<UUID> getSignalSourceProcessIDs() {
        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        UUID signalID;

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessCommunicationDefinition processCommunication = this.init(process);

            signalID = processCommunication.getSignalID();
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }

        if (ValueUtil.isAnyNullOrEmpty(signalID)) {
            throw new StatusNotExistedException();
        }

        PathDefinition path
                = new PathDefinition(List.of(new IdentifierDefinition("Signals"), new IdentifierDefinition(signalID)));

        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
        InfoObject signalInfo = objectManager.get(path);

        SignalContentObject signalContent = (SignalContentObject) signalInfo.getContent();
        Set<UUID> sourceProcessIDs = signalContent.getSourceProcessIDs();

        return sourceProcessIDs;
    }

    public void setSignalSourceProcessIDs(Set<UUID> sourceProcessIDs) {
        if (ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        UUID signalID;

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessCommunicationDefinition processCommunication = this.init(process);

            signalID = processCommunication.getSignalID();
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }

        if (ValueUtil.isAnyNullOrEmpty(signalID)) {
            throw new StatusNotExistedException();
        }

        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);

        PathDefinition path
                = new PathDefinition(List.of(new IdentifierDefinition("Signals"), new IdentifierDefinition(signalID)));

        InfoObject signalInfo = objectManager.get(path);

        SecurityDescriptorObject securityDescriptor = signalInfo.getSecurityDescriptor();
        Set<AccessControlDefinition> permissions = new HashSet<>();
        AccessControlDefinition permission = new AccessControlDefinition();
        permission.setUserId(new UserIDDefinition(this.base.getId(), UserType.PROCESS));
        permission.setScope(AccessControlScopeType.THIS);
        permission.setValue(PermissionType.FULLCONTROL_ALLOW);
        permissions.add(permission);
        permission = new AccessControlDefinition();
        permission.setUserId(new UserIDDefinition(this.base.getId(), UserType.PARENT_PROCESS));
        permission.setScope(AccessControlScopeType.THIS);
        permission.setValue(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
        permissions.add(permission);
        for (UUID sourceProcessID : sourceProcessIDs) {
            permission = new AccessControlDefinition();
            permission.setUserId(new UserIDDefinition(sourceProcessID, UserType.PROCESS));
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
            permissions.add(permission);
        }
        securityDescriptor.setPermissions(permissions);

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessInfoTableObject currentProcessInfoTable = currentProcess.getInfoTable();

        boolean contain = currentProcessInfoTable.containById(signalInfo.getId());
        if (!contain) {
            signalInfo.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);
        }
        SignalContentObject signalContent = (SignalContentObject) signalInfo.getContent();
        signalContent.setSourceProcessIDs(sourceProcessIDs);
        if (!contain) {
            signalInfo.close();
        }
    }

    public List<SignalEntryDefinition> receiveSignals() {
        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        UUID signalID;

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessCommunicationDefinition processCommunication = this.init(process);

            signalID = processCommunication.getSignalID();
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }

        if (ValueUtil.isAnyNullOrEmpty(signalID)) {
            throw new StatusNotExistedException();
        }

        PathDefinition path
                = new PathDefinition(List.of(new IdentifierDefinition("Signals"), new IdentifierDefinition(signalID)));

        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
        InfoObject signalInfo = objectManager.get(path);

        SignalContentObject signalContent = (SignalContentObject) signalInfo.getContent();
        List<SignalEntryDefinition> signalEntries = signalContent.receive();

        ProcessStatisticsObject processStatistics = this.base.getStatistics();
        processStatistics.addSignalReadCount(signalEntries.size());
        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
        ThreadObject thread = threadManager.getCurrent();
        ThreadStatisticsObject threadStatistics = thread.getStatistics();
        threadStatistics.addSignalReadCount(signalEntries.size());

        return CollectionUtil.unmodifiable(signalEntries);
    }

    public void sendSignal(UUID signalID, long key, long value) {
        if (ValueUtil.isAnyNullOrEmpty(signalID)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        PathDefinition path
                = new PathDefinition(List.of(new IdentifierDefinition("Signals"), new IdentifierDefinition(signalID)));

        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
        InfoObject signalInfo = objectManager.get(path);

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessInfoTableObject processInfoTable = process.getInfoTable();

        boolean contain = processInfoTable.containById(signalInfo.getId());
        if (!contain) {
            signalInfo.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);
        }
        SignalContentObject signalContent = (SignalContentObject) signalInfo.getContent();
        signalContent.send(key, value);
        if (!contain) {
            signalInfo.close();
        }

        ProcessStatisticsObject processStatistics = this.base.getStatistics();
        processStatistics.addSignalWriteCount(1);
        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
        ThreadObject thread = threadManager.getCurrent();
        ThreadStatisticsObject threadStatistics = thread.getStatistics();
        threadStatistics.addSignalWriteCount(1);
    }
}
