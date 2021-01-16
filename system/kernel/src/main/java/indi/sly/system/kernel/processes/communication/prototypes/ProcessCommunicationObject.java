package indi.sly.system.kernel.processes.communication.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessPrototype;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoStatusOpenAttributeType;
import indi.sly.system.kernel.processes.communication.values.ProcessCommunicationDefinition;
import indi.sly.system.kernel.processes.communication.instances.prototypes.PortContentObject;
import indi.sly.system.kernel.processes.communication.instances.prototypes.SignalContentObject;
import indi.sly.system.kernel.processes.communication.instances.values.SignalEntryDefinition;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessStatisticsObject;
import indi.sly.system.kernel.processes.values.ProcessStatusType;
import indi.sly.system.kernel.processes.values.ProcessTokenLimitType;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.types.AccessControlTypes;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;
import indi.sly.system.kernel.security.types.PrivilegeTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCommunicationObject extends ABytesValueProcessPrototype<ProcessCommunicationDefinition> {
    private ProcessObject process;

    public void setProcess(ProcessObject process) {
        this.process = process;
    }

    public byte[] getShared() {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeType(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        this.init();

        byte[] processCommunicationShared = this.value.getShared();

        ProcessStatisticsObject processStatistics = this.process.getStatistics();
        processStatistics.addSharedReadCount(1);
        processStatistics.addSharedReadBytes(processCommunicationShared.length);

        return processCommunicationShared;
    }

    public void setShared(byte[] shared) {
        if (ObjectUtil.isAnyNull(shared)) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeType(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        ProcessTokenObject processToken = this.process.getToken();
        if (shared.length > processToken.getLimits().get(ProcessTokenLimitType.SHARED_LENGTH_MAX)) {
            throw new ConditionPermissionsException();
        }

        this.value.setShared(shared);

        ProcessStatisticsObject processStatistics = this.process.getStatistics();
        processStatistics.addSharedWriteCount(1);
        processStatistics.addSharedWriteBytes(shared.length);
    }

    public Set<UUID> getPortIDs() {
        this.init();

        return Collections.unmodifiableSet(this.value.getPortIDs());
    }

    public UUID createPort(Set<UUID> sourceProcessIDs) {
        if (ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeType(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        UUID portID = null;

        try {
            this.lock(LockType.WRITE);
            this.init();

            ProcessTokenObject processToken = this.process.getToken();
            if (this.value.getPortIDs().size() > processToken.getLimits().get(ProcessTokenLimitType.PORT_COUNT_MAX)) {
                throw new ConditionPermissionsException();
            }

            List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Ports"));

            InfoObject ports = objectManager.get(identifications);

            UUID typeID =
                    this.factoryManager.getKernelSpace().getConfiguration().PROCESSES_COMMUNICATION_INSTANCE_PORT_ID;

            InfoObject port = ports.createChildAndOpen(UUID.randomUUID(), new IdentificationDefinition(typeID),
                    InfoStatusOpenAttributeType.OPEN_EXCLUSIVE);
            SecurityDescriptorObject securityDescriptor = port.getSecurityDescriptor();
            Map<UUID, Long> accessControl = new HashMap<>();
            accessControl.put(processToken.getAccountID(), AccessControlTypes.FULLCONTROL_ALLOW);
            accessControl.put(this.factoryManager.getKernelSpace().getConfiguration().SECURITY_GROUP_USERS_ID,
                    AccessControlTypes.CREATECHILD_WRITEDATA_ALLOW);
            securityDescriptor.setAccessControlTypes(accessControl);
            PortContentObject portContent = (PortContentObject) port.getContent();
            portContent.setSourceProcessIDs(sourceProcessIDs);
            port.close();

            portID = port.getID();

            this.value.getPortIDs().add(portID);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        ProcessStatisticsObject processStatistics = this.process.getStatistics();
        processStatistics.addPortCount(1);

        return portID;
    }

    public void deleteAllPort() {
        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeType(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Set<UUID> processCommunicationPortIDs = this.value.getPortIDs();

            ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

            for (UUID processCommunicationPortID : processCommunicationPortIDs) {
                List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Ports"));

                InfoObject ports = objectManager.get(identifications);
                ports.deleteChild(new IdentificationDefinition(processCommunicationPortID));

                processCommunicationPortIDs.remove(processCommunicationPortID);
            }

            this.fresh();
            this.lock(LockType.NONE);
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void deletePort(UUID portID) {
        if (ObjectUtil.isAnyNull(portID)) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeType(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Set<UUID> processCommunicationPortIDs = this.value.getPortIDs();
            if (processCommunicationPortIDs.contains(portID)) {
                throw new StatusNotExistedException();
            }

            ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

            List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Ports"));

            InfoObject ports = objectManager.get(identifications);
            ports.deleteChild(new IdentificationDefinition(portID));

            processCommunicationPortIDs.remove(portID);

            this.fresh();
            this.lock(LockType.NONE);
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public Set<UUID> getPortSourceProcessIDs(UUID portID) {
        if (ObjectUtil.isAnyNull(portID)) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeType(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        this.init();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Ports"), new IdentificationDefinition(portID));

        InfoObject port = objectManager.get(identifications);
        port.open(InfoStatusOpenAttributeType.OPEN_ONLY_READ);
        PortContentObject portContent = (PortContentObject) port.getContent();
        Set<UUID> sourceProcessIDs = portContent.getSourceProcessIDs();
        port.close();

        return sourceProcessIDs;
    }

    public void setPortSourceProcessIDs(UUID portID, Set<UUID> sourceProcessIDs) {
        if (ValueUtil.isAnyNullOrEmpty(portID) || ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeType(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        this.init();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Ports"), new IdentificationDefinition(portID));

        InfoObject port = objectManager.get(identifications);
        port.open(InfoStatusOpenAttributeType.OPEN_SHARED_WRITE);
        PortContentObject portContent = (PortContentObject) port.getContent();
        portContent.setSourceProcessIDs(sourceProcessIDs);
        port.close();
    }

    public byte[] receivePort(UUID portID) {
        if (ObjectUtil.isAnyNull(portID)) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeType(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        this.init();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        UUID signalID = this.value.getSignalID();

        List<IdentificationDefinition> identifications = new ArrayList<>();
        identifications.add(new IdentificationDefinition("Ports"));
        identifications.add(new IdentificationDefinition(portID));

        InfoObject port = objectManager.get(identifications);
        port.open(InfoStatusOpenAttributeType.OPEN_SHARED_WRITE);
        PortContentObject portContent = (PortContentObject) port.getContent();
        byte[] value = portContent.receive();
        port.close();

        ProcessStatisticsObject processStatistics = this.process.getStatistics();
        processStatistics.addPortReadCount(1);
        processStatistics.addPortReadBytes(value.length);

        return value;
    }

    public void sendPort(UUID portID, byte[] value) {
        if (ValueUtil.isAnyNullOrEmpty(portID) || ObjectUtil.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Ports"), new IdentificationDefinition(portID));

        InfoObject port = objectManager.get(identifications);
        port.open(InfoStatusOpenAttributeType.OPEN_SHARED_WRITE);
        PortContentObject portContent = (PortContentObject) port.getContent();
        portContent.send(value);
        port.close();

        ProcessStatisticsObject processStatistics = this.process.getStatistics();
        processStatistics.addPortWriteCount(1);
        processStatistics.addPortWriteBytes(value.length);
    }

    public UUID getSignalID() {
        this.init();

        return this.value.getSignalID();
    }

    public void createSignal(Set<UUID> sourceProcessIDs) {
        if (ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeType(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (!ValueUtil.isAnyNullOrEmpty(this.value.getSignalID())) {
                throw new StatusAlreadyFinishedException();
            }

            ProcessTokenObject processToken = this.process.getToken();

            ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

            List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Signals"));

            InfoObject signals = objectManager.get(identifications);

            UUID typeID =
                    this.factoryManager.getKernelSpace().getConfiguration().PROCESSES_COMMUNICATION_INSTANCE_SIGNAL_ID;

            InfoObject signal = signals.createChildAndOpen(UUID.randomUUID(), new IdentificationDefinition(typeID),
                    InfoStatusOpenAttributeType.OPEN_EXCLUSIVE);
            SecurityDescriptorObject securityDescriptor = signal.getSecurityDescriptor();
            Map<UUID, Long> accessControl = new HashMap<>();
            accessControl.put(processToken.getAccountID(), AccessControlTypes.FULLCONTROL_ALLOW);
            accessControl.put(this.factoryManager.getKernelSpace().getConfiguration().SECURITY_GROUP_USERS_ID,
                    AccessControlTypes.CREATECHILD_WRITEDATA_ALLOW);
            securityDescriptor.setAccessControlTypes(accessControl);
            SignalContentObject signalContent = (SignalContentObject) signal.getContent();
            signalContent.setSourceProcessIDs(sourceProcessIDs);
            signal.close();

            this.value.setSignalID(signals.getID());

            this.fresh();
            this.lock(LockType.NONE);
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void deleteSignal() {
        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeType(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (!ValueUtil.isAnyNullOrEmpty(this.value.getSignalID())) {
                throw new StatusAlreadyFinishedException();
            }

            UUID signalID = this.value.getSignalID();

            ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

            List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Signals"));

            InfoObject signals = objectManager.get(identifications);
            signals.deleteChild(new IdentificationDefinition(signalID));

            this.value.setSignalID(null);

            this.fresh();
            this.lock(LockType.NONE);
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public Set<UUID> getSignalSourceProcessIDs() {
        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeType(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        this.init();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        UUID signalID = this.value.getSignalID();

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Signals"), new IdentificationDefinition(signalID));

        InfoObject signal = objectManager.get(identifications);
        signal.open(InfoStatusOpenAttributeType.OPEN_ONLY_READ);
        SignalContentObject signalContent = (SignalContentObject) signal.getContent();
        Set<UUID> sourceProcessIDs = signalContent.getSourceProcessIDs();
        signal.close();

        return sourceProcessIDs;
    }

    public void setSignalSourceProcessIDs(Set<UUID> sourceProcessIDs) {
        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeType(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        if (ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        this.init();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        UUID signalID = this.value.getSignalID();

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Signals"), new IdentificationDefinition(signalID));

        InfoObject signal = objectManager.get(identifications);
        signal.open(InfoStatusOpenAttributeType.OPEN_SHARED_WRITE);
        SignalContentObject signalContent = (SignalContentObject) signal.getContent();
        signalContent.setSourceProcessIDs(sourceProcessIDs);
        signal.close();
    }

    public List<SignalEntryDefinition> receiveSignals() {
        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeType(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        this.init();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        UUID signalID = this.value.getSignalID();

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Signals"), new IdentificationDefinition(signalID));

        InfoObject signal = objectManager.get(identifications);
        signal.open(InfoStatusOpenAttributeType.OPEN_SHARED_WRITE);
        SignalContentObject signalContent = (SignalContentObject) signal.getContent();
        List<SignalEntryDefinition> signalEntries = signalContent.receive();
        signal.close();

        ProcessStatisticsObject processStatistics = this.process.getStatistics();
        processStatistics.addSignalReadCount(signalEntries.size());

        return signalEntries;
    }

    public void sendSignal(ProcessObject targetProcess, long key, long value) {
        if (ObjectUtil.isAnyNull(targetProcess)) {
            throw new ConditionParametersException();
        }

        UUID signalID = targetProcess.getCommunication().getSignalID();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Signals"), new IdentificationDefinition(signalID));

        InfoObject signal = objectManager.get(identifications);
        signal.open(InfoStatusOpenAttributeType.OPEN_SHARED_WRITE);
        SignalContentObject signalContent = (SignalContentObject) signal.getContent();
        signalContent.send(key, value);
        signal.close();

        ProcessStatisticsObject processStatistics = this.process.getStatistics();
        processStatistics.addSignalWriteCount(1);
    }
}
