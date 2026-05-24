package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.*;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.prototypes.AChildCacheableObject;
import indi.sly.system.kernel.core.values.APersistentEntity;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.CommunicationRepositoryObject;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.values.PortDefinition;
import indi.sly.system.kernel.processes.values.SignalDefinition;
import indi.sly.system.kernel.processes.values.SignalEntryDefinition;
import indi.sly.system.kernel.processes.lang.ProcessProcessorReadComponentFunction;
import indi.sly.system.kernel.processes.lang.ProcessProcessorWriteComponentConsumer;
import indi.sly.system.kernel.processes.prototypes.mediators.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.*;
import org.redisson.api.RBucket;
import org.redisson.api.RSet;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCommunicationObject extends AChildCacheableObject<ProcessChildCacheEntity, ProcessObject> {
    protected ProcessFactory factory;
    protected ProcessProcessorMediator processorMediator;

    private ProcessEntity getSelf() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getProcess().getProcessId())) {
            throw new ConditionContextException();
        }

        return this.processorMediator.getSelf().apply(this.cache.getProcess().getProcessId());
    }

    private ProcessCommunicationEntity init(ProcessEntity process) {
        Set<ProcessProcessorReadComponentFunction> resolvers = this.processorMediator.getReadProcessCommunications();

        APersistentEntity source = null;

        for (ProcessProcessorReadComponentFunction resolver : resolvers) {
            source = resolver.apply(source, process);
        }

        return (ProcessCommunicationEntity) source;
    }

    private void flush(ProcessEntity process, ProcessCommunicationEntity value) {
        Set<ProcessProcessorWriteComponentConsumer> resolvers = this.processorMediator.getWriteProcessCommunications();

        for (ProcessProcessorWriteComponentConsumer resolver : resolvers) {
            resolver.accept(process, value);
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

        this.factory.lockProcess(this.cache.getProcess(), LockType.READ);
        try {
            ProcessCommunicationEntity processCommunication = this.init(process);

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

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessCommunicationEntity processCommunication = this.init(process);

            processCommunication.setShared(shared);

            this.flush(process, processCommunication);
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

    public Set<UUID> getPortIds() {
        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CommunicationRepositoryObject communicationRepository = memoryManager.getCommunicationRepository();

        this.factory.lockProcess(this.cache.getProcess(), LockType.READ);
        try {
            RSet<UUID> processCommunicationPortSet = communicationRepository.getSet("Process", this.base.getId(), "Communication_Ports");

            return CollectionUtil.unmodifiable(processCommunicationPortSet);
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

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CommunicationRepositoryObject communicationRepository = memoryManager.getCommunicationRepository();

        ProcessTokenObject processToken = this.base.getToken();

        UUID portId;

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            RSet<UUID> processCommunicationPorts = communicationRepository.getSet("Process", this.base.getId(), "Communication_Ports");

            if (processCommunicationPorts.size() > processToken.getLimits().get(ProcessTokenLimitType.PORT_COUNT_MAX)) {
                throw new ConditionRefuseException();
            }

            portId = UUIDUtil.createRandom();
            PortDefinition port = new PortDefinition();

            port.setProcessId(this.base.getId());
            port.getSourceProcessIds().addAll(sourceProcessIDs);
            port.setLimit(processToken.getLimits().get(ProcessTokenLimitType.PORT_LENGTH_MAX));

            RBucket<PortDefinition> portBucket = communicationRepository.getBucket("Port", portId, null);

            portBucket.set(port);
            processCommunicationPorts.add(portId);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }

        ProcessStatisticsObject processStatistics = this.base.getStatistics();
        processStatistics.addPortCount(1);
        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
        ThreadObject thread = threadManager.getCurrent();
        ThreadStatisticsObject threadStatistics = thread.getStatistics();
        threadStatistics.addPortCount(1);

        return portId;
    }

    public void deleteAllPort() {
        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CommunicationRepositoryObject communicationRepository = memoryManager.getCommunicationRepository();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            RSet<UUID> processCommunicationPorts = communicationRepository.getSet("Process", this.base.getId(), "Communication_Ports");

            RBucket<PortDefinition> portBucket;
            for (UUID processCommunicationPort : processCommunicationPorts) {
                portBucket = communicationRepository.getBucket("Port", processCommunicationPort, null);
                portBucket.delete();
            }

            processCommunicationPorts.clear();
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void deletePort(UUID portId) {
        if (ObjectUtil.isAnyNull(portId)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CommunicationRepositoryObject communicationRepository = memoryManager.getCommunicationRepository();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            RSet<UUID> processCommunicationPorts = communicationRepository.getSet("Process", this.base.getId(), "Communication_Ports");

            if (!processCommunicationPorts.contains(portId)) {
                throw new StatusNotExistedException();
            }

            RBucket<PortDefinition> portBucket = communicationRepository.getBucket("Port", portId, null);
            portBucket.delete();

            processCommunicationPorts.remove(portId);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public Set<UUID> getPortSourceProcessIDs(UUID portId) {
        if (ObjectUtil.isAnyNull(portId)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CommunicationRepositoryObject communicationRepository = memoryManager.getCommunicationRepository();

        this.factory.lockProcess(this.cache.getProcess(), LockType.READ);
        try {
            RSet<UUID> processCommunicationPorts = communicationRepository.getSet("Process", this.base.getId(), "Communication_Ports");

            if (!processCommunicationPorts.contains(portId)) {
                throw new StatusNotExistedException();
            }

            RBucket<PortDefinition> portBucket = communicationRepository.getBucket("Port", portId, null);
            if (!portBucket.isExists()) {
                throw new StatusNotExistedException();
            }

            return CollectionUtil.unmodifiable(portBucket.get().getSourceProcessIds());
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public void setPortSourceProcessIDs(UUID portId, Set<UUID> sourceProcessIDs) {
        if (ValueUtil.isAnyNullOrEmpty(portId) || ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CommunicationRepositoryObject communicationRepository = memoryManager.getCommunicationRepository();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            RSet<UUID> processCommunicationPorts = communicationRepository.getSet("Process", this.base.getId(), "Communication_Ports");

            if (!processCommunicationPorts.contains(portId)) {
                throw new StatusNotExistedException();
            }

            RBucket<PortDefinition> portBucket = communicationRepository.getBucket("Port", portId, null);
            if (!portBucket.isExists()) {
                throw new StatusNotExistedException();
            }

            PortDefinition port = portBucket.get();
            port.getSourceProcessIds().clear();
            port.getSourceProcessIds().addAll(sourceProcessIDs);

            portBucket.set(port);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public byte[] receivePort(UUID portId) {
        if (ObjectUtil.isAnyNull(portId)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CommunicationRepositoryObject communicationRepository = memoryManager.getCommunicationRepository();

        byte[] value;

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            RSet<UUID> processCommunicationPorts = communicationRepository.getSet("Process", this.base.getId(), "Communication_Ports");

            if (!processCommunicationPorts.contains(portId)) {
                throw new StatusNotExistedException();
            }

            RBucket<PortDefinition> portBucket = communicationRepository.getBucket("Port", portId, null);
            if (!portBucket.isExists()) {
                throw new StatusNotExistedException();
            }

            PortDefinition port = portBucket.get();
            value = port.getValue();
            port.setValue(ArrayUtil.EMPTY_BYTES);

            portBucket.set(port);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }

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

    public void sendPort(UUID portId, byte[] value) {
        if (ValueUtil.isAnyNullOrEmpty(portId) || ObjectUtil.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CommunicationRepositoryObject communicationRepository = memoryManager.getCommunicationRepository();

        RBucket<PortDefinition> portBucket = communicationRepository.getBucket("Port", portId, null);

        if (!portBucket.isExists()) {
            throw new StatusNotExistedException();
        }

        PortDefinition port = portBucket.get();

        if (!port.getSourceProcessIds().contains(this.base.getId()) && !port.getProcessId().equals(this.base.getId())) {
            throw new StatusRelationshipErrorException();
        }
        if (port.size() + value.length >= port.getLimit()) {
            throw new StatusInsufficientResourcesException();
        }
        port.setValue(ArrayUtil.combineBytes(port.getValue(), value));

        portBucket.set(port);

        ProcessStatisticsObject processStatistics = this.base.getStatistics();
        processStatistics.addPortWriteCount(1);
        processStatistics.addPortWriteBytes(value.length);
        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
        ThreadObject thread = threadManager.getCurrent();
        ThreadStatisticsObject threadStatistics = thread.getStatistics();
        threadStatistics.addPortWriteCount(1);
        threadStatistics.addPortWriteBytes(value.length);
    }

    public UUID getSignalId() {
        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CommunicationRepositoryObject communicationRepository = memoryManager.getCommunicationRepository();

        this.factory.lockProcess(this.cache.getProcess(), LockType.READ);
        try {
            RBucket<UUID> processCommunicationSignalBucket = communicationRepository.getBucket("Process", this.base.getId(), "Communication_Signal");

            if (!processCommunicationSignalBucket.isExists()) {
                return null;
            } else {
                return processCommunicationSignalBucket.get();
            }
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

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CommunicationRepositoryObject communicationRepository = memoryManager.getCommunicationRepository();

        ProcessTokenObject processToken = this.base.getToken();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            RBucket<SignalDefinition> processCommunicationSignalBucket = communicationRepository.getBucket("Process", this.base.getId(), "Communication_Signal");

            if (processCommunicationSignalBucket.isExists()) {
                throw new StatusAlreadyExistedException();
            }

            SignalDefinition signal = new SignalDefinition();

            signal.setProcessId(this.base.getId());
            signal.getSourceProcessIds().addAll(sourceProcessIDs);
            signal.setLimit(processToken.getLimits().get(ProcessTokenLimitType.SIGNAL_LENGTH_MAX));

            processCommunicationSignalBucket.set(signal);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void deleteSignal() {
        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CommunicationRepositoryObject communicationRepository = memoryManager.getCommunicationRepository();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            RBucket<SignalDefinition> processCommunicationSignalBucket = communicationRepository.getBucket("Process", this.base.getId(), "Communication_Signal");

            processCommunicationSignalBucket.delete();
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public Set<UUID> getSignalSourceProcessIDs() {
        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CommunicationRepositoryObject communicationRepository = memoryManager.getCommunicationRepository();

        this.factory.lockProcess(this.cache.getProcess(), LockType.READ);
        try {
            RBucket<SignalDefinition> processCommunicationSignalBucket = communicationRepository.getBucket("Process", this.base.getId(), "Communication_Signal");

            if (!processCommunicationSignalBucket.isExists()) {
                throw new StatusNotExistedException();
            }

            return CollectionUtil.unmodifiable(processCommunicationSignalBucket.get().getSourceProcessIds());
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public void setSignalSourceProcessIDs(Set<UUID> sourceProcessIDs) {
        if (ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CommunicationRepositoryObject communicationRepository = memoryManager.getCommunicationRepository();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            RBucket<SignalDefinition> processCommunicationSignalBucket = communicationRepository.getBucket("Process", this.base.getId(), "Communication_Signal");

            if (!processCommunicationSignalBucket.isExists()) {
                throw new StatusNotExistedException();
            }

            SignalDefinition signal = processCommunicationSignalBucket.get();
            signal.getSourceProcessIds().clear();
            signal.getSourceProcessIds().addAll(sourceProcessIDs);

            processCommunicationSignalBucket.set(signal);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public List<SignalEntryDefinition> receiveSignals() {
        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CommunicationRepositoryObject communicationRepository = memoryManager.getCommunicationRepository();

        DateTimeObject dateTime = this.coreManager.getDateTime();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            RBucket<SignalDefinition> processCommunicationSignalBucket = communicationRepository.getBucket("Process", this.base.getId(), "Communication_Signal");

            if (!processCommunicationSignalBucket.isExists()) {
                throw new StatusNotExistedException();
            }

            SignalDefinition signal = processCommunicationSignalBucket.get();
            List<SignalEntryDefinition> signalEntries = signal.pollAll();
            long nowDateTime = dateTime.getCurrent();
            for (SignalEntryDefinition signalEntry : signalEntries) {
                signalEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);
            }

            return CollectionUtil.unmodifiable(signalEntries);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void sendSignal(UUID signalID, long key, long value) {
        if (ValueUtil.isAnyNullOrEmpty(signalID)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CommunicationRepositoryObject communicationRepository = memoryManager.getCommunicationRepository();

        DateTimeObject dateTime = this.coreManager.getDateTime();

        RBucket<SignalDefinition> processCommunicationSignalBucket = communicationRepository.getBucket("Process", this.base.getId(), "Communication_Signal");

        if (!processCommunicationSignalBucket.isExists()) {
            throw new StatusNotExistedException();
        }

        SignalDefinition signal = processCommunicationSignalBucket.get();

        if (!signal.getSourceProcessIds().contains(this.base.getId()) && !signal.getProcessId().equals(this.base.getId())) {
            throw new StatusRelationshipErrorException();
        }
        if (signal.size() >= signal.getLimit()) {
            throw new StatusInsufficientResourcesException();
        }
        SignalEntryDefinition signalEntry = new SignalEntryDefinition();
        long nowDateTime = dateTime.getCurrent();
        signalEntry.setSource(this.base.getId());
        signalEntry.setKey(key);
        signalEntry.setValue(value);
        signalEntry.getDate().put(DateTimeType.CREATE, nowDateTime);
        signalEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);
        signal.add(signalEntry);
        processCommunicationSignalBucket.set(signal);

        ProcessStatisticsObject processStatistics = this.base.getStatistics();
        processStatistics.addSignalWriteCount(1);
        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
        ThreadObject thread = threadManager.getCurrent();
        ThreadStatisticsObject threadStatistics = thread.getStatistics();
        threadStatistics.addSignalWriteCount(1);
    }
}
