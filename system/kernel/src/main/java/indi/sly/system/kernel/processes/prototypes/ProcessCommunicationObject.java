package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.processes.instances.prototypes.PortContentObject;
import indi.sly.system.kernel.processes.instances.prototypes.SignalContentObject;
import indi.sly.system.kernel.processes.instances.values.SignalEntryDefinition;
import indi.sly.system.kernel.processes.values.ProcessCommunicationDefinition;
import indi.sly.system.kernel.processes.values.ProcessStatusType;
import indi.sly.system.kernel.processes.values.ProcessTokenLimitType;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;
import indi.sly.system.kernel.security.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCommunicationObject extends ABytesValueProcessObject<ProcessCommunicationDefinition, ProcessObject> {
    public byte[] getShared() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        if (!this.parent.isCurrent()) {
            ProcessTokenObject processToken = this.parent.getToken();

            if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_COMMUNICATION)) {
                throw new ConditionRefuseException();
            }
        }

        this.init();

        byte[] processCommunicationShared = this.value.getShared();

        ProcessStatisticsObject processStatistics = this.parent.getStatistics();
        processStatistics.addSharedReadCount(1);
        processStatistics.addSharedReadBytes(processCommunicationShared.length);

        return processCommunicationShared;
    }

    public void setShared(byte[] shared) {
        if (ObjectUtil.isAnyNull(shared)) {
            throw new ConditionParametersException();
        }

        if (!this.parent.isCurrent()) {
            ProcessTokenObject processToken = this.parent.getToken();

            if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_COMMUNICATION)) {
                throw new ConditionRefuseException();
            }
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
            this.lock(LockType.NONE);
        }

        ProcessStatisticsObject processStatistics = this.parent.getStatistics();
        processStatistics.addSharedWriteCount(1);
        processStatistics.addSharedWriteBytes(shared.length);
    }

    public Set<UUID> getPortIDs() {
        this.init();

        return CollectionUtil.unmodifiable(this.value.getPortIDs());
    }

    public UUID createPort(Set<UUID> sourceProcessIDs) {
        if (ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        if (!this.parent.isCurrent()) {
            ProcessTokenObject processToken = this.parent.getToken();

            if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_COMMUNICATION)) {
                throw new ConditionRefuseException();
            }
        }

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        UUID portID;

        try {
            this.lock(LockType.WRITE);
            this.init();

            ProcessTokenObject processToken = this.parent.getToken();
            if (this.value.getPortIDs().size() > processToken.getLimits().get(ProcessTokenLimitType.PORT_COUNT_MAX)) {
                throw new ConditionRefuseException();
            }

            List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Ports"));

            InfoObject ports = objectManager.get(identifications);

            UUID typeID = this.factoryManager.getKernelSpace().getConfiguration().PROCESSES_COMMUNICATION_INSTANCE_PORT_ID;

            InfoObject port = ports.createChildAndOpen(typeID, new IdentificationDefinition(UUID.randomUUID()),
                    InfoOpenAttributeType.OPEN_EXCLUSIVE);
            SecurityDescriptorObject securityDescriptor = port.getSecurityDescriptor();
            Set<AccessControlDefinition> permissions = new HashSet<>();
            AccessControlDefinition permission = new AccessControlDefinition();
            permission.getUserID().setID(processToken.getAccountID());
            permission.getUserID().setType(UserType.ACCOUNT);
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(PermissionType.FULLCONTROL_ALLOW);
            permissions.add(permission);
            permission = new AccessControlDefinition();
            permission.getUserID().setID(this.factoryManager.getKernelSpace().getConfiguration().SECURITY_GROUP_USERS_ID);
            permission.getUserID().setType(UserType.GROUP);
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
            permissions.add(permission);
            securityDescriptor.setPermissions(permissions);
            PortContentObject portContent = (PortContentObject) port.getContent();
            portContent.setSourceProcessIDs(sourceProcessIDs);
            port.close();

            portID = port.getID();

            this.value.getPortIDs().add(portID);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        ProcessStatisticsObject processStatistics = this.parent.getStatistics();
        processStatistics.addPortCount(1);

        return portID;
    }

    public void deleteAllPort() {
        if (!this.parent.isCurrent()) {
            ProcessTokenObject processToken = this.parent.getToken();

            if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_COMMUNICATION)) {
                throw new ConditionRefuseException();
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

        if (!this.parent.isCurrent()) {
            ProcessTokenObject processToken = this.parent.getToken();

            if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_COMMUNICATION)) {
                throw new ConditionRefuseException();
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

        if (!this.parent.isCurrent()) {
            ProcessTokenObject processToken = this.parent.getToken();

            if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_COMMUNICATION)) {
                throw new ConditionRefuseException();
            }
        }

        this.init();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Ports"), new IdentificationDefinition(portID));

        InfoObject port = objectManager.get(identifications);
        port.open(InfoOpenAttributeType.OPEN_ONLY_READ);
        PortContentObject portContent = (PortContentObject) port.getContent();
        Set<UUID> sourceProcessIDs = portContent.getSourceProcessIDs();
        port.close();

        return sourceProcessIDs;
    }

    public void setPortSourceProcessIDs(UUID portID, Set<UUID> sourceProcessIDs) {
        if (ValueUtil.isAnyNullOrEmpty(portID) || ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        if (!this.parent.isCurrent()) {
            ProcessTokenObject processToken = this.parent.getToken();

            if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_COMMUNICATION)) {
                throw new ConditionRefuseException();
            }
        }

        this.init();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Ports"), new IdentificationDefinition(portID));

        InfoObject port = objectManager.get(identifications);
        port.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);
        PortContentObject portContent = (PortContentObject) port.getContent();
        portContent.setSourceProcessIDs(sourceProcessIDs);
        port.close();
    }

    public byte[] receivePort(UUID portID) {
        if (ObjectUtil.isAnyNull(portID)) {
            throw new ConditionParametersException();
        }

        if (!this.parent.isCurrent()) {
            ProcessTokenObject processToken = this.parent.getToken();

            if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_COMMUNICATION)) {
                throw new ConditionRefuseException();
            }
        }

        this.init();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        UUID signalID = this.value.getSignalID();

        List<IdentificationDefinition> identifications = new ArrayList<>();
        identifications.add(new IdentificationDefinition("Ports"));
        identifications.add(new IdentificationDefinition(portID));

        InfoObject port = objectManager.get(identifications);
        port.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);
        PortContentObject portContent = (PortContentObject) port.getContent();
        byte[] value = portContent.receive();
        port.close();

        ProcessStatisticsObject processStatistics = this.parent.getStatistics();
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
        port.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);
        PortContentObject portContent = (PortContentObject) port.getContent();
        portContent.send(value);
        port.close();

        ProcessStatisticsObject processStatistics = this.parent.getStatistics();
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

        if (!this.parent.isCurrent()) {
            ProcessTokenObject processToken = this.parent.getToken();

            if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_COMMUNICATION)) {
                throw new ConditionRefuseException();
            }
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (!ValueUtil.isAnyNullOrEmpty(this.value.getSignalID())) {
                throw new StatusAlreadyFinishedException();
            }

            ProcessTokenObject processToken = this.parent.getToken();

            ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

            List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Signals"));

            InfoObject signals = objectManager.get(identifications);

            UUID typeID = this.factoryManager.getKernelSpace().getConfiguration().PROCESSES_COMMUNICATION_INSTANCE_SIGNAL_ID;

            InfoObject signal = signals.createChildAndOpen(typeID, new IdentificationDefinition(UUID.randomUUID()),
                    InfoOpenAttributeType.OPEN_EXCLUSIVE);
            SecurityDescriptorObject securityDescriptor = signal.getSecurityDescriptor();
            Set<AccessControlDefinition> permissions = new HashSet<>();
            AccessControlDefinition permission = new AccessControlDefinition();
            permission.getUserID().setID(processToken.getAccountID());
            permission.getUserID().setType(UserType.ACCOUNT);
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(PermissionType.FULLCONTROL_ALLOW);
            permissions.add(permission);
            permission = new AccessControlDefinition();
            permission.getUserID().setID(this.factoryManager.getKernelSpace().getConfiguration().SECURITY_GROUP_USERS_ID);
            permission.getUserID().setType(UserType.GROUP);
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
            permissions.add(permission);
            securityDescriptor.setPermissions(permissions);
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
        if (!this.parent.isCurrent()) {
            ProcessTokenObject processToken = this.parent.getToken();

            if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_COMMUNICATION)) {
                throw new ConditionRefuseException();
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
        if (!this.parent.isCurrent()) {
            ProcessTokenObject processToken = this.parent.getToken();

            if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_COMMUNICATION)) {
                throw new ConditionRefuseException();
            }
        }

        this.init();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        UUID signalID = this.value.getSignalID();

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Signals"), new IdentificationDefinition(signalID));

        InfoObject signal = objectManager.get(identifications);
        signal.open(InfoOpenAttributeType.OPEN_ONLY_READ);
        SignalContentObject signalContent = (SignalContentObject) signal.getContent();
        Set<UUID> sourceProcessIDs = signalContent.getSourceProcessIDs();
        signal.close();

        return sourceProcessIDs;
    }

    public void setSignalSourceProcessIDs(Set<UUID> sourceProcessIDs) {
        if (!this.parent.isCurrent()) {
            ProcessTokenObject processToken = this.parent.getToken();

            if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_COMMUNICATION)) {
                throw new ConditionRefuseException();
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
        signal.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);
        SignalContentObject signalContent = (SignalContentObject) signal.getContent();
        signalContent.setSourceProcessIDs(sourceProcessIDs);
        signal.close();
    }

    public List<SignalEntryDefinition> receiveSignals() {
        if (!this.parent.isCurrent()) {
            ProcessTokenObject processToken = this.parent.getToken();

            if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_COMMUNICATION)) {
                throw new ConditionRefuseException();
            }
        }

        this.init();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        UUID signalID = this.value.getSignalID();

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Signals"), new IdentificationDefinition(signalID));

        InfoObject signal = objectManager.get(identifications);
        signal.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);
        SignalContentObject signalContent = (SignalContentObject) signal.getContent();
        List<SignalEntryDefinition> signalEntries = signalContent.receive();
        signal.close();

        ProcessStatisticsObject processStatistics = this.parent.getStatistics();
        processStatistics.addSignalReadCount(signalEntries.size());

        return CollectionUtil.unmodifiable(signalEntries);
    }

    public void sendSignal(ProcessObject targetProcess, long key, long value) {
        if (ObjectUtil.isAnyNull(targetProcess)) {
            throw new ConditionParametersException();
        }

        UUID signalID = targetProcess.getCommunication().getSignalID();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Signals"), new IdentificationDefinition(signalID));

        InfoObject signal = objectManager.get(identifications);
        signal.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);
        SignalContentObject signalContent = (SignalContentObject) signal.getContent();
        signalContent.send(key, value);
        signal.close();

        ProcessStatisticsObject processStatistics = this.parent.getStatistics();
        processStatistics.addSignalWriteCount(1);
    }
}
