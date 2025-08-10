package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.instances.prototypes.PortContentObject;
import indi.sly.system.kernel.processes.instances.prototypes.SignalContentObject;
import indi.sly.system.kernel.processes.instances.values.SignalEntryDefinition;
import indi.sly.system.kernel.processes.values.ProcessCommunicationDefinition;
import indi.sly.system.kernel.processes.values.ProcessStatusType;
import indi.sly.system.kernel.processes.values.ProcessTokenLimitType;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;
import indi.sly.system.kernel.security.values.AccessControlDefinition;
import indi.sly.system.kernel.security.values.AccessControlScopeType;
import indi.sly.system.kernel.security.values.PermissionType;
import indi.sly.system.kernel.security.values.UserType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCommunicationObject extends ABytesValueProcessObject<ProcessCommunicationDefinition, ProcessObject> {
    public byte[] getShared() {
        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
        ThreadObject thread = threadManager.getCurrent();

        try {
            this.lock(LockType.READ);
            this.init();

            byte[] processCommunicationShared = this.value.getShared();

            ProcessStatisticsObject processStatistics = this.parent.getStatistics();
            processStatistics.addSharedReadCount(1);
            processStatistics.addSharedReadBytes(processCommunicationShared.length);
            ThreadStatisticsObject threadStatistics = thread.getStatistics();
            threadStatistics.addSharedReadCount(1);
            threadStatistics.addSharedReadBytes(processCommunicationShared.length);

            return processCommunicationShared;
        } finally {
            this.unlock(LockType.READ);
        }
    }

    public void setShared(byte[] shared) {
        if (ObjectUtil.isAnyNull(shared)) {
            throw new ConditionParametersException();
        }

        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessTokenObject processToken = this.parent.getToken();
        if (shared.length > processToken.getLimits().get(ProcessTokenLimitType.SHARED_LENGTH_MAX)) {
            throw new ConditionRefuseException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.setShared(shared);

            this.fresh();
        } finally {
            this.unlock(LockType.WRITE);
        }

        ProcessStatisticsObject processStatistics = this.parent.getStatistics();
        processStatistics.addSharedWriteCount(1);
        processStatistics.addSharedWriteBytes(shared.length);
        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
        ThreadObject thread = threadManager.getCurrent();
        ThreadStatisticsObject threadStatistics = thread.getStatistics();
        threadStatistics.addSharedWriteCount(1);
        threadStatistics.addSharedWriteBytes(shared.length);
    }

    public Set<UUID> getPortIDs() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.READ);
            this.init();

            return CollectionUtil.unmodifiable(this.value.getPortIDs());
        } finally {
            this.unlock(LockType.READ);
        }
    }

    public UUID createPort(Set<UUID> sourceProcessIDs) {
        if (ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Ports"));

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        UUID portID;

        try {
            this.lock(LockType.WRITE);
            this.init();

            ProcessTokenObject processToken = this.parent.getToken();
            if (this.value.getPortIDs().size() > processToken.getLimits().get(ProcessTokenLimitType.PORT_COUNT_MAX)) {
                throw new ConditionRefuseException();
            }

            InfoObject portsInfo = objectManager.get(identifications);

            InfoObject portInfo = portsInfo.createChildAndOpen(kernelConfiguration.PROCESSES_COMMUNICATION_INSTANCE_PORT_ID,
                    new IdentificationDefinition(UUID.randomUUID()), InfoOpenAttributeType.OPEN_EXCLUSIVE);

            SecurityDescriptorObject securityDescriptor = portInfo.getSecurityDescriptor();
            Set<AccessControlDefinition> permissions = new HashSet<>();
            AccessControlDefinition permission = new AccessControlDefinition();
            permission.getUserID().setID(this.parent.getID());
            permission.getUserID().setType(UserType.PROCESS);
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(PermissionType.FULLCONTROL_ALLOW);
            permissions.add(permission);
            permission = new AccessControlDefinition();
            permission.getUserID().setID(this.parent.getID());
            permission.getUserID().setType(UserType.PARENT_PROCESS);
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
            permissions.add(permission);
            for (UUID sourceProcessID : sourceProcessIDs) {
                permission = new AccessControlDefinition();
                permission.getUserID().setID(sourceProcessID);
                permission.getUserID().setType(UserType.PROCESS);
                permission.setScope(AccessControlScopeType.THIS);
                permission.setValue(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
                permissions.add(permission);
            }
            securityDescriptor.setPermissions(permissions);

            PortContentObject portContent = (PortContentObject) portInfo.getContent();
            portContent.setSourceProcessIDs(sourceProcessIDs);

            ProcessInfoTableObject processInfoTable = this.parent.getInfoTable();
            ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(portInfo.getID());
            processInfoEntry.setUnsupportedDelete(true);

            portID = portInfo.getID();

            this.value.getPortIDs().add(portID);

            this.fresh();
        } finally {
            this.unlock(LockType.WRITE);
        }

        ProcessStatisticsObject processStatistics = this.parent.getStatistics();
        processStatistics.addPortCount(1);
        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
        ThreadObject thread = threadManager.getCurrent();
        ThreadStatisticsObject threadStatistics = thread.getStatistics();
        threadStatistics.addPortCount(1);

        return portID;
    }

    public void deleteAllPort() {
        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Set<UUID> processCommunicationPortIDs = this.value.getPortIDs();

            ProcessInfoTableObject processInfoTable = this.parent.getInfoTable();

            for (UUID processCommunicationPortID : processCommunicationPortIDs) {
                if (processInfoTable.containByID(processCommunicationPortID)) {
                    ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(processCommunicationPortID);
                    processInfoEntry.setUnsupportedDelete(false);

                    InfoObject info = processInfoEntry.getInfo();
                    info.close();
                }

                processCommunicationPortIDs.remove(processCommunicationPortID);
            }

            this.fresh();
        } finally {
            this.unlock(LockType.WRITE);
        }
    }

    public void deletePort(UUID portID) {
        if (ObjectUtil.isAnyNull(portID)) {
            throw new ConditionParametersException();
        }

        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Set<UUID> processCommunicationPortIDs = this.value.getPortIDs();
            if (processCommunicationPortIDs.contains(portID)) {
                throw new StatusNotExistedException();
            }

            ProcessInfoTableObject processInfoTable = this.parent.getInfoTable();
            if (processInfoTable.containByID(portID)) {
                ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(portID);
                processInfoEntry.setUnsupportedDelete(false);

                InfoObject info = processInfoEntry.getInfo();
                info.close();
            }

            processCommunicationPortIDs.remove(portID);

            this.fresh();
        } finally {
            this.unlock(LockType.WRITE);
        }
    }

    public Set<UUID> getPortSourceProcessIDs(UUID portID) {
        if (ObjectUtil.isAnyNull(portID)) {
            throw new ConditionParametersException();
        }

        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        if (!this.getPortIDs().contains(portID)) {
            throw new StatusNotExistedException();
        }

        List<IdentificationDefinition> identifications
                = List.of(new IdentificationDefinition("Ports"), new IdentificationDefinition(portID));

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
        InfoObject portInfo = objectManager.get(identifications);

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessInfoTableObject processInfoTable = process.getInfoTable();

        boolean contain = processInfoTable.containByID(portInfo.getID());
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

        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        if (!this.getPortIDs().contains(portID)) {
            throw new StatusNotExistedException();
        }

        List<IdentificationDefinition> identifications
                = List.of(new IdentificationDefinition("Ports"), new IdentificationDefinition(portID));

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
        InfoObject portInfo = objectManager.get(identifications);

        SecurityDescriptorObject securityDescriptor = portInfo.getSecurityDescriptor();
        Set<AccessControlDefinition> permissions = new HashSet<>();
        AccessControlDefinition permission = new AccessControlDefinition();
        permission.getUserID().setID(this.parent.getID());
        permission.getUserID().setType(UserType.PROCESS);
        permission.setScope(AccessControlScopeType.THIS);
        permission.setValue(PermissionType.FULLCONTROL_ALLOW);
        permissions.add(permission);
        permission = new AccessControlDefinition();
        permission.getUserID().setID(this.parent.getID());
        permission.getUserID().setType(UserType.PARENT_PROCESS);
        permission.setScope(AccessControlScopeType.THIS);
        permission.setValue(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
        permissions.add(permission);
        for (UUID sourceProcessID : sourceProcessIDs) {
            permission = new AccessControlDefinition();
            permission.getUserID().setID(sourceProcessID);
            permission.getUserID().setType(UserType.PROCESS);
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
            permissions.add(permission);
        }
        securityDescriptor.setPermissions(permissions);

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessInfoTableObject processInfoTable = process.getInfoTable();

        boolean contain = processInfoTable.containByID(portInfo.getID());
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

        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        if (!this.getPortIDs().contains(portID)) {
            throw new StatusNotExistedException();
        }

        List<IdentificationDefinition> identifications
                = List.of(new IdentificationDefinition("Ports"), new IdentificationDefinition(portID));

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
        InfoObject portInfo = objectManager.get(identifications);

        PortContentObject portContent = (PortContentObject) portInfo.getContent();
        byte[] value = portContent.receive();

        ProcessStatisticsObject processStatistics = this.parent.getStatistics();
        processStatistics.addPortReadCount(1);
        processStatistics.addPortReadBytes(value.length);
        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
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

        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        List<IdentificationDefinition> identifications
                = List.of(new IdentificationDefinition("Ports"), new IdentificationDefinition(portID));

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
        InfoObject portInfo = objectManager.get(identifications);

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessInfoTableObject processInfoTable = process.getInfoTable();

        boolean contain = processInfoTable.containByID(portInfo.getID());
        if (!contain) {
            portInfo.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);
        }
        PortContentObject portContent = (PortContentObject) portInfo.getContent();
        portContent.send(value);
        if (!contain) {
            portInfo.close();
        }

        ProcessStatisticsObject processStatistics = this.parent.getStatistics();
        processStatistics.addPortWriteCount(1);
        processStatistics.addPortWriteBytes(value.length);
        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
        ThreadObject thread = threadManager.getCurrent();
        ThreadStatisticsObject threadStatistics = thread.getStatistics();
        threadStatistics.addPortWriteCount(1);
        threadStatistics.addPortWriteBytes(value.length);
    }

    public UUID getSignalID() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getSignalID();
        } finally {
            this.unlock(LockType.READ);
        }
    }

    public void createSignal(Set<UUID> sourceProcessIDs) {
        if (ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Signals"));

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (!ValueUtil.isAnyNullOrEmpty(this.value.getSignalID())) {
                throw new StatusAlreadyFinishedException();
            }

            InfoObject signalsInfo = objectManager.get(identifications);

            InfoObject signalInfo = signalsInfo.createChildAndOpen(kernelConfiguration.PROCESSES_COMMUNICATION_INSTANCE_SIGNAL_ID,
                    new IdentificationDefinition(UUID.randomUUID()), InfoOpenAttributeType.OPEN_SHARED_WRITE);

            SecurityDescriptorObject securityDescriptor = signalInfo.getSecurityDescriptor();
            Set<AccessControlDefinition> permissions = new HashSet<>();
            AccessControlDefinition permission = new AccessControlDefinition();
            permission.getUserID().setID(this.parent.getID());
            permission.getUserID().setType(UserType.PROCESS);
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(PermissionType.FULLCONTROL_ALLOW);
            permissions.add(permission);
            permission = new AccessControlDefinition();
            permission.getUserID().setID(this.parent.getID());
            permission.getUserID().setType(UserType.PARENT_PROCESS);
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
            permissions.add(permission);
            for (UUID sourceProcessID : sourceProcessIDs) {
                permission = new AccessControlDefinition();
                permission.getUserID().setID(sourceProcessID);
                permission.getUserID().setType(UserType.PROCESS);
                permission.setScope(AccessControlScopeType.THIS);
                permission.setValue(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
                permissions.add(permission);
            }
            securityDescriptor.setPermissions(permissions);

            SignalContentObject signalContent = (SignalContentObject) signalInfo.getContent();
            signalContent.setSourceProcessIDs(sourceProcessIDs);

            ProcessInfoTableObject processInfoTable = this.parent.getInfoTable();
            ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(signalInfo.getID());
            processInfoEntry.setUnsupportedDelete(true);

            this.value.setSignalID(signalInfo.getID());

            this.fresh();
        } finally {
            this.unlock(LockType.WRITE);
        }
    }

    public void deleteSignal() {
        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            UUID signalID = this.value.getSignalID();

            if (ValueUtil.isAnyNullOrEmpty(signalID)) {
                throw new StatusAlreadyFinishedException();
            }

            ProcessInfoTableObject processInfoTable = this.parent.getInfoTable();
            if (processInfoTable.containByID(signalID)) {
                ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(signalID);
                processInfoEntry.setUnsupportedDelete(false);

                InfoObject info = processInfoEntry.getInfo();
                info.close();
            }

            this.value.setSignalID(null);

            this.fresh();
        } finally {
            this.unlock(LockType.WRITE);
        }
    }

    public Set<UUID> getSignalSourceProcessIDs() {
        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        UUID signalID;

        try {
            this.lock(LockType.READ);
            this.init();

            signalID = this.value.getSignalID();
        } finally {
            this.unlock(LockType.READ);
        }

        if (ValueUtil.isAnyNullOrEmpty(signalID)) {
            throw new StatusNotExistedException();
        }

        List<IdentificationDefinition> identifications
                = List.of(new IdentificationDefinition("Signals"), new IdentificationDefinition(signalID));

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
        InfoObject signalInfo = objectManager.get(identifications);

        SignalContentObject signalContent = (SignalContentObject) signalInfo.getContent();
        Set<UUID> sourceProcessIDs = signalContent.getSourceProcessIDs();

        return sourceProcessIDs;
    }

    public void setSignalSourceProcessIDs(Set<UUID> sourceProcessIDs) {
        if (ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        UUID signalID;

        try {
            this.lock(LockType.WRITE);
            this.init();

            signalID = this.value.getSignalID();
        } finally {
            this.unlock(LockType.WRITE);
        }

        if (ValueUtil.isAnyNullOrEmpty(signalID)) {
            throw new StatusNotExistedException();
        }

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<IdentificationDefinition> identifications
                = List.of(new IdentificationDefinition("Signals"), new IdentificationDefinition(signalID));

        InfoObject signalInfo = objectManager.get(identifications);

        SecurityDescriptorObject securityDescriptor = signalInfo.getSecurityDescriptor();
        Set<AccessControlDefinition> permissions = new HashSet<>();
        AccessControlDefinition permission = new AccessControlDefinition();
        permission.getUserID().setID(this.parent.getID());
        permission.getUserID().setType(UserType.PROCESS);
        permission.setScope(AccessControlScopeType.THIS);
        permission.setValue(PermissionType.FULLCONTROL_ALLOW);
        permissions.add(permission);
        permission = new AccessControlDefinition();
        permission.getUserID().setID(this.parent.getID());
        permission.getUserID().setType(UserType.PARENT_PROCESS);
        permission.setScope(AccessControlScopeType.THIS);
        permission.setValue(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
        permissions.add(permission);
        for (UUID sourceProcessID : sourceProcessIDs) {
            permission = new AccessControlDefinition();
            permission.getUserID().setID(sourceProcessID);
            permission.getUserID().setType(UserType.PROCESS);
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
            permissions.add(permission);
        }
        securityDescriptor.setPermissions(permissions);

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessInfoTableObject processInfoTable = process.getInfoTable();

        boolean contain = processInfoTable.containByID(signalInfo.getID());
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
        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        UUID signalID;

        try {
            this.lock(LockType.READ);
            this.init();

            signalID = this.value.getSignalID();
        } finally {
            this.unlock(LockType.READ);
        }

        if (ValueUtil.isAnyNullOrEmpty(signalID)) {
            throw new StatusNotExistedException();
        }

        List<IdentificationDefinition> identifications
                = List.of(new IdentificationDefinition("Signals"), new IdentificationDefinition(signalID));

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
        InfoObject signalInfo = objectManager.get(identifications);

        SignalContentObject signalContent = (SignalContentObject) signalInfo.getContent();
        List<SignalEntryDefinition> signalEntries = signalContent.receive();

        ProcessStatisticsObject processStatistics = this.parent.getStatistics();
        processStatistics.addSignalReadCount(signalEntries.size());
        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
        ThreadObject thread = threadManager.getCurrent();
        ThreadStatisticsObject threadStatistics = thread.getStatistics();
        threadStatistics.addSignalReadCount(signalEntries.size());

        return CollectionUtil.unmodifiable(signalEntries);
    }

    public void sendSignal(UUID signalID, long key, long value) {
        if (ValueUtil.isAnyNullOrEmpty(signalID)) {
            throw new ConditionParametersException();
        }

        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        List<IdentificationDefinition> identifications
                = List.of(new IdentificationDefinition("Signals"), new IdentificationDefinition(signalID));

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
        InfoObject signalInfo = objectManager.get(identifications);

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessInfoTableObject processInfoTable = process.getInfoTable();

        boolean contain = processInfoTable.containByID(signalInfo.getID());
        if (!contain) {
            signalInfo.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);
        }
        SignalContentObject signalContent = (SignalContentObject) signalInfo.getContent();
        signalContent.send(key, value);
        if (!contain) {
            signalInfo.close();
        }

        ProcessStatisticsObject processStatistics = this.parent.getStatistics();
        processStatistics.addSignalWriteCount(1);
        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
        ThreadObject thread = threadManager.getCurrent();
        ThreadStatisticsObject threadStatistics = thread.getStatistics();
        threadStatistics.addSignalWriteCount(1);
    }
}
