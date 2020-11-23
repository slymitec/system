package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.exceptions.ConditionContextException;
import indi.sly.system.common.exceptions.StatusRelationshipErrorException;
import indi.sly.system.common.functions.Consumer2;
import indi.sly.system.common.functions.Function2;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.entities.ProcessEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessObject extends ACoreObject {
    protected ProcessObjectFactoryObject factory;
    protected ProcessObjectProcessorRegister processorRegister;

    protected UUID id;

    public UUID getID() {
        if (UUIDUtils.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.id;
    }

    private synchronized ProcessEntity getProcess() {
        return this.processorRegister.getProcess().apply(this.id);
    }

    public UUID getParentProcessID() {
        return this.getProcess().getParentProcessID();
    }

    public UUID getSessionID() {
        return this.getProcess().getSessionID();
    }

    public synchronized ProcessStatusObject getStatus() {
        ProcessEntity process = this.getProcess();

        ProcessStatusObject processStatus = this.factoryManager.create(ProcessStatusObject.class);

        this.processorRegister.getReadProcessStatuses();

        processStatus.processorRegister = this.processorRegister;
        processStatus.setProcess(process);
        processStatus.setProcessObject(this);

        return processStatus;
    }

    public synchronized ProcessCommunicationObject getCommunication() {
        ProcessEntity process = this.getProcess();

        ProcessCommunicationObject processCommunication = this.factoryManager.create(ProcessCommunicationObject.class);

        processCommunication.setSource(() -> {
            List<Function2<byte[], byte[], ProcessEntity>> funcs =
                    this.processorRegister.getReadProcessCommunications();

            byte[] source = null;

            for (Function2<byte[], byte[], ProcessEntity> pair : funcs) {
                source = pair.apply(source, process);
            }

            return source;
        }, (byte[] source) -> {
            List<Consumer2<ProcessEntity, byte[]>> funcs = this.processorRegister.getWriteProcessCommunications();

            for (Consumer2<ProcessEntity, byte[]> pair : funcs) {
                pair.accept(process, source);
            }
        });
        processCommunication.setLock((lockType) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            processRepository.lock(process, lockType);
        });

        return processCommunication;
    }

    public synchronized ProcessContextObject getContext() {
        ProcessEntity process = this.getProcess();

        ProcessContextObject processContext = this.factoryManager.create(ProcessContextObject.class);

        processContext.setSource(() -> {
            List<Function2<byte[], byte[], ProcessEntity>> funcs = this.processorRegister.getReadProcessContexts();

            byte[] source = null;

            for (Function2<byte[], byte[], ProcessEntity> pair : funcs) {
                source = pair.apply(source, process);
            }

            return source;
        }, (byte[] source) -> {
            List<Consumer2<ProcessEntity, byte[]>> funcs = this.processorRegister.getWriteProcessContexts();

            for (Consumer2<ProcessEntity, byte[]> pair : funcs) {
                pair.accept(process, source);
            }
        });
        processContext.setLock((lockType) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            processRepository.lock(process, lockType);
        });

        return processContext;
    }

    public synchronized ProcessHandleTableObject getHandleTable() {
        ProcessEntity process = this.getProcess();

        ProcessHandleTableObject processHandleTable = this.factoryManager.create(ProcessHandleTableObject.class);

        processHandleTable.setSource(() -> {
            List<Function2<byte[], byte[], ProcessEntity>> funcs = this.processorRegister.getReadProcessHandleTables();

            byte[] source = null;

            for (Function2<byte[], byte[], ProcessEntity> pair : funcs) {
                source = pair.apply(source, process);
            }

            return source;
        }, (byte[] source) -> {
            List<Consumer2<ProcessEntity, byte[]>> funcs = this.processorRegister.getWriteProcessHandleTables();

            for (Consumer2<ProcessEntity, byte[]> pair : funcs) {
                pair.accept(process, source);
            }
        });
        processHandleTable.setLock((lockType) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            processRepository.lock(process, lockType);
        });

        return processHandleTable;
    }

    public synchronized ProcessStatisticsObject getStatistics() {
        ProcessEntity process = this.getProcess();

        ProcessStatisticsObject processStatistics = this.factoryManager.create(ProcessStatisticsObject.class);

        processStatistics.setSource(() -> {
            List<Function2<byte[], byte[], ProcessEntity>> funcs = this.processorRegister.getReadProcessStatistics();

            byte[] source = null;

            for (Function2<byte[], byte[], ProcessEntity> pair : funcs) {
                source = pair.apply(source, process);
            }

            return source;
        }, (byte[] source) -> {
            List<Consumer2<ProcessEntity, byte[]>> funcs = this.processorRegister.getWriteProcessStatistics();

            for (Consumer2<ProcessEntity, byte[]> pair : funcs) {
                pair.accept(process, source);
            }
        });
        processStatistics.setLock((lockType) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            processRepository.lock(process, lockType);
        });

        return processStatistics;
    }

    public synchronized ProcessTokenObject getToken() {
        ProcessEntity process = this.getProcess();

        ProcessTokenObject processToken = this.factoryManager.create(ProcessTokenObject.class);

        processToken.setSource(() -> {
            List<Function2<byte[], byte[], ProcessEntity>> funcs = this.processorRegister.getReadProcessTokens();

            byte[] source = null;

            for (Function2<byte[], byte[], ProcessEntity> pair : funcs) {
                source = pair.apply(source, process);
            }

            return source;
        }, (byte[] source) -> {
            List<Consumer2<ProcessEntity, byte[]>> funcs = this.processorRegister.getWriteProcessTokens();

            for (Consumer2<ProcessEntity, byte[]> pair : funcs) {
                pair.accept(process, source);
            }
        });
        processToken.setLock((lockType) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            processRepository.lock(process, lockType);
        });
        processToken.setProcess(this);

        return processToken;
    }
}
