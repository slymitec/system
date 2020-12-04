package indi.sly.system.kernel.processes.communication.prototypes;

import indi.sly.system.common.exceptions.*;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.LogicalUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.prototypes.ABytesProcessPrototype;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.types.InfoStatusOpenAttributeTypes;
import indi.sly.system.kernel.processes.communication.values.ProcessCommunicationDefinition;
import indi.sly.system.kernel.processes.communication.instances.prototypes.PortContentObject;
import indi.sly.system.kernel.processes.communication.instances.prototypes.SignalContentObject;
import indi.sly.system.kernel.processes.communication.instances.values.SignalEntryDefinition;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessStatisticsObject;
import indi.sly.system.kernel.processes.types.ProcessStatusTypes;
import indi.sly.system.kernel.processes.types.ProcessTokenLimitTypes;
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
public class ProcessCommunicationObject extends ABytesProcessPrototype {
    @Override
    protected void read(byte[] source) {
        this.processCommunication = ObjectUtils.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtils.transferToByteArray(this.processCommunication);
    }

    private ProcessCommunicationDefinition processCommunication;
    private ProcessObject process;

    public void setProcess(ProcessObject process) {
        this.process = process;
    }

    public byte[] getShared() {
        if (LogicalUtils.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        this.init();

        byte[] processCommunicationShared = this.processCommunication.getShared();

        ProcessStatisticsObject processStatistics = this.process.getStatistics();
        processStatistics.addSharedReadCount(1);
        processStatistics.addSharedReadBytes(processCommunicationShared.length);

        return processCommunicationShared;
    }

    public void setShared(byte[] shared) {
        if (ObjectUtils.isAnyNull(shared)) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        ProcessTokenObject processToken = this.process.getToken();
        if (shared.length > processToken.getLimits().get(ProcessTokenLimitTypes.SHARED_LENGTH_MAX)) {
            throw new ConditionPermissionsException();
        }

        this.processCommunication.setShared(shared);

        ProcessStatisticsObject processStatistics = this.process.getStatistics();
        processStatistics.addSharedWriteCount(1);
        processStatistics.addSharedWriteBytes(shared.length);
    }

    public Set<UUID> getPortIDs() {
        this.init();

        return Collections.unmodifiableSet(this.processCommunication.getPortIDs());
    }

    public UUID createPort(Set<UUID> sourceProcessIDs) {
        if (ObjectUtils.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        UUID portID = null;

        try {
            this.lock(LockTypes.WRITE);
            this.init();

            ProcessTokenObject processToken = this.process.getToken();
            if (this.processCommunication.getPortIDs().size() > processToken.getLimits().get(ProcessTokenLimitTypes.PORT_COUNT_MAX)) {
                throw new ConditionPermissionsException();
            }

            List<Identification> identifications = new ArrayList<>();
            identifications.add(new Identification("Ports"));

            InfoObject ports = objectManager.get(identifications);

            UUID typeID =
                    this.factoryManager.getKernelSpace().getConfiguration().PROCESSES_COMMUNICATION_INSTANCE_PORT_ID;

            InfoObject port = ports.createChildAndOpen(UUID.randomUUID(), new Identification(typeID),
                    InfoStatusOpenAttributeTypes.OPEN_EXCLUSIVE);
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

            this.processCommunication.getPortIDs().add(portID);

            this.fresh();
        } catch (AKernelException exception) {
            throw exception;
        } finally {
            this.lock(LockTypes.NONE);
        }

        ProcessStatisticsObject processStatistics = this.process.getStatistics();
        processStatistics.addPortCount(1);

        return portID;
    }

    public void deleteAllPort() {
        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        try {
            this.lock(LockTypes.WRITE);
            this.init();

            Set<UUID> processCommunicationPortIDs = this.processCommunication.getPortIDs();

            ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

            for (UUID processCommunicationPortID : processCommunicationPortIDs) {
                List<Identification> identifications = new ArrayList<>();
                identifications.add(new Identification("Ports"));

                InfoObject ports = objectManager.get(identifications);
                ports.deleteChild(new Identification(processCommunicationPortID));

                processCommunicationPortIDs.remove(processCommunicationPortID);
            }

            this.fresh();
            this.lock(LockTypes.NONE);
        } catch (AKernelException exception) {
            throw exception;
        } finally {
            this.lock(LockTypes.NONE);
        }
    }

    public void deletePort(UUID portID) {
        if (ObjectUtils.isAnyNull(portID)) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        try {
            this.lock(LockTypes.WRITE);
            this.init();

            Set<UUID> processCommunicationPortIDs = this.processCommunication.getPortIDs();
            if (processCommunicationPortIDs.contains(portID)) {
                throw new StatusNotExistedException();
            }

            ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

            List<Identification> identifications = new ArrayList<>();
            identifications.add(new Identification("Ports"));

            InfoObject ports = objectManager.get(identifications);
            ports.deleteChild(new Identification(portID));

            processCommunicationPortIDs.remove(portID);

            this.fresh();
            this.lock(LockTypes.NONE);
        } catch (AKernelException exception) {
            throw exception;
        } finally {
            this.lock(LockTypes.NONE);
        }
    }

    public Set<UUID> getPortSourceProcessIDs(UUID portID) {
        if (ObjectUtils.isAnyNull(portID)) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        this.init();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<Identification> identifications = new ArrayList<>();
        identifications.add(new Identification("Ports"));
        identifications.add(new Identification(portID));

        InfoObject port = objectManager.get(identifications);
        port.open(InfoStatusOpenAttributeTypes.OPEN_ONLYREAD);
        PortContentObject portContent = (PortContentObject) port.getContent();
        Set<UUID> sourceProcessIDs = portContent.getSourceProcessIDs();
        port.close();

        return sourceProcessIDs;
    }

    public void setPortSourceProcessIDs(UUID portID, Set<UUID> sourceProcessIDs) {
        if (UUIDUtils.isAnyNullOrEmpty(portID) || ObjectUtils.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        this.init();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<Identification> identifications = new ArrayList<>();
        identifications.add(new Identification("Ports"));
        identifications.add(new Identification(portID));

        InfoObject port = objectManager.get(identifications);
        port.open(InfoStatusOpenAttributeTypes.OPEN_SHARED_WRITE);
        PortContentObject portContent = (PortContentObject) port.getContent();
        portContent.setSourceProcessIDs(sourceProcessIDs);
        port.close();
    }

    public byte[] receivePort(UUID portID) {
        if (ObjectUtils.isAnyNull(portID)) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        this.init();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        UUID signalID = this.processCommunication.getSignalID();

        List<Identification> identifications = new ArrayList<>();
        identifications.add(new Identification("Ports"));
        identifications.add(new Identification(portID));

        InfoObject port = objectManager.get(identifications);
        port.open(InfoStatusOpenAttributeTypes.OPEN_SHARED_WRITE);
        PortContentObject portContent = (PortContentObject) port.getContent();
        byte[] value = portContent.receive();
        port.close();

        ProcessStatisticsObject processStatistics = this.process.getStatistics();
        processStatistics.addPortReadCount(1);
        processStatistics.addPortReadBytes(value.length);

        return value;
    }

    public void sendPort(UUID portID, byte[] value) {
        if (UUIDUtils.isAnyNullOrEmpty(portID) || ObjectUtils.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<Identification> identifications = new ArrayList<>();
        identifications.add(new Identification("Ports"));
        identifications.add(new Identification(portID));

        InfoObject port = objectManager.get(identifications);
        port.open(InfoStatusOpenAttributeTypes.OPEN_SHARED_WRITE);
        PortContentObject portContent = (PortContentObject) port.getContent();
        portContent.send(value);
        port.close();

        ProcessStatisticsObject processStatistics = this.process.getStatistics();
        processStatistics.addPortWriteCount(1);
        processStatistics.addPortWriteBytes(value.length);
    }

    public UUID getSignalID() {
        this.init();

        return this.processCommunication.getSignalID();
    }

    public void createSignal(Set<UUID> sourceProcessIDs) {
        if (ObjectUtils.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        try {
            this.lock(LockTypes.WRITE);
            this.init();

            if (!UUIDUtils.isAnyNullOrEmpty(this.processCommunication.getSignalID())) {
                throw new StatusAlreadyFinishedException();
            }

            ProcessTokenObject processToken = this.process.getToken();

            ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

            List<Identification> identifications = new ArrayList<>();
            identifications.add(new Identification("Signals"));

            InfoObject signals = objectManager.get(identifications);

            UUID typeID =
                    this.factoryManager.getKernelSpace().getConfiguration().PROCESSES_COMMUNICATION_INSTANCE_SIGNAL_ID;

            InfoObject signal = signals.createChildAndOpen(UUID.randomUUID(), new Identification(typeID),
                    InfoStatusOpenAttributeTypes.OPEN_EXCLUSIVE);
            SecurityDescriptorObject securityDescriptor = signal.getSecurityDescriptor();
            Map<UUID, Long> accessControl = new HashMap<>();
            accessControl.put(processToken.getAccountID(), AccessControlTypes.FULLCONTROL_ALLOW);
            accessControl.put(this.factoryManager.getKernelSpace().getConfiguration().SECURITY_GROUP_USERS_ID,
                    AccessControlTypes.CREATECHILD_WRITEDATA_ALLOW);
            securityDescriptor.setAccessControlTypes(accessControl);
            SignalContentObject signalContent = (SignalContentObject) signal.getContent();
            signalContent.setSourceProcessIDs(sourceProcessIDs);
            signal.close();

            this.processCommunication.setSignalID(signals.getID());

            this.fresh();
            this.lock(LockTypes.NONE);
        } catch (AKernelException exception) {
            throw exception;
        } finally {
            this.lock(LockTypes.NONE);
        }
    }

    public void deleteSignal() {
        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        try {
            this.lock(LockTypes.WRITE);
            this.init();

            if (!UUIDUtils.isAnyNullOrEmpty(this.processCommunication.getSignalID())) {
                throw new StatusAlreadyFinishedException();
            }

            UUID signalID = this.processCommunication.getSignalID();

            ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

            List<Identification> identifications = new ArrayList<>();
            identifications.add(new Identification("Signals"));

            InfoObject signals = objectManager.get(identifications);
            signals.deleteChild(new Identification(signalID));

            this.processCommunication.setSignalID(null);

            this.fresh();
            this.lock(LockTypes.NONE);
        } catch (AKernelException exception) {
            throw exception;
        } finally {
            this.lock(LockTypes.NONE);
        }
    }

    public Set<UUID> getSignalSourceProcessIDs() {
        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        this.init();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        UUID signalID = this.processCommunication.getSignalID();

        List<Identification> identifications = new ArrayList<>();
        identifications.add(new Identification("Signals"));
        identifications.add(new Identification(signalID));

        InfoObject signal = objectManager.get(identifications);
        signal.open(InfoStatusOpenAttributeTypes.OPEN_ONLYREAD);
        SignalContentObject signalContent = (SignalContentObject) signal.getContent();
        Set<UUID> sourceProcessIDs = signalContent.getSourceProcessIDs();
        signal.close();

        return sourceProcessIDs;
    }

    public void setSignalSourceProcessIDs(Set<UUID> sourceProcessIDs) {
        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        if (ObjectUtils.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        this.init();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        UUID signalID = this.processCommunication.getSignalID();

        List<Identification> identifications = new ArrayList<>();
        identifications.add(new Identification("Signals"));
        identifications.add(new Identification(signalID));

        InfoObject signal = objectManager.get(identifications);
        signal.open(InfoStatusOpenAttributeTypes.OPEN_SHARED_WRITE);
        SignalContentObject signalContent = (SignalContentObject) signal.getContent();
        signalContent.setSourceProcessIDs(sourceProcessIDs);
        signal.close();
    }

    public List<SignalEntryDefinition> receiveSignals() {
        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        this.init();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        UUID signalID = this.processCommunication.getSignalID();

        List<Identification> identifications = new ArrayList<>();
        identifications.add(new Identification("Signals"));
        identifications.add(new Identification(signalID));

        InfoObject signal = objectManager.get(identifications);
        signal.open(InfoStatusOpenAttributeTypes.OPEN_SHARED_WRITE);
        SignalContentObject signalContent = (SignalContentObject) signal.getContent();
        List<SignalEntryDefinition> signalEntries = signalContent.receive();
        signal.close();

        ProcessStatisticsObject processStatistics = this.process.getStatistics();
        processStatistics.addSignalReadCount(signalEntries.size());

        return signalEntries;
    }

    public void sendSignal(ProcessObject targetProcess, long key, long value) {
        if (ObjectUtils.isAnyNull(targetProcess)) {
            throw new ConditionParametersException();
        }

        UUID signalID = targetProcess.getCommunication().getSignalID();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<Identification> identifications = new ArrayList<>();
        identifications.add(new Identification("Signals"));
        identifications.add(new Identification(signalID));

        InfoObject signal = objectManager.get(identifications);
        signal.open(InfoStatusOpenAttributeTypes.OPEN_SHARED_WRITE);
        SignalContentObject signalContent = (SignalContentObject) signal.getContent();
        signalContent.send(key, value);
        signal.close();

        ProcessStatisticsObject processStatistics = this.process.getStatistics();
        processStatistics.addSignalWriteCount(1);
    }
}
