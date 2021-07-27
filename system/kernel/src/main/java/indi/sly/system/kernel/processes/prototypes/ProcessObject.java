package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionContextException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.lang.ReadProcessComponentFunction;
import indi.sly.system.kernel.processes.lang.WriteProcessComponentConsumer;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessObject extends APrototype {
    protected ProcessFactory factory;
    protected ProcessProcessorMediator processorMediator;

    protected UUID id;

    public UUID getID() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.id;
    }

    public boolean isCurrent() {
        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

        ThreadObject thread = threadManager.getCurrent();

        return this.id.equals(thread.getProcessID());
    }

    private synchronized ProcessEntity getProcess() {
        return this.processorMediator.getProcess().apply(this.id);
    }

    public UUID getParentProcessID() {
        return this.getProcess().getParentProcessID();
    }

    public synchronized ProcessStatusObject getStatus() {
        ProcessEntity process = this.getProcess();

        ProcessStatusObject processStatus = this.factoryManager.create(ProcessStatusObject.class);

        processStatus.processorMediator = this.processorMediator;
        processStatus.setSource(() -> process, (ProcessEntity source) -> {
        });
        processStatus.process = this;

        return processStatus;
    }

    public synchronized ProcessCommunicationObject getCommunication() {
        ProcessEntity process = this.getProcess();

        ProcessCommunicationObject processCommunication = this.factoryManager.create(ProcessCommunicationObject.class);

        processCommunication.setSource(() -> {
            Set<ReadProcessComponentFunction> funcs = this.processorMediator.getReadProcessCommunications();

            byte[] source = null;

            for (ReadProcessComponentFunction pair : funcs) {
                source = pair.apply(source, process);
            }

            return source;
        }, (byte[] source) -> {
            Set<WriteProcessComponentConsumer> funcs = this.processorMediator.getWriteProcessCommunications();

            for (WriteProcessComponentConsumer pair : funcs) {
                pair.accept(process, source);
            }
        });
        processCommunication.setLock((lock) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            processRepository.lock(process, lock);
        });
        processCommunication.process = this;

        return processCommunication;
    }

    public synchronized ProcessContextObject getContext() {
        ProcessEntity process = this.getProcess();

        ProcessContextObject processContext = this.factoryManager.create(ProcessContextObject.class);

        processContext.setSource(() -> {
            Set<ReadProcessComponentFunction> funcs = this.processorMediator.getReadProcessContexts();

            byte[] source = null;

            for (ReadProcessComponentFunction pair : funcs) {
                source = pair.apply(source, process);
            }

            return source;
        }, (byte[] source) -> {
            Set<WriteProcessComponentConsumer> funcs = this.processorMediator.getWriteProcessContexts();

            for (WriteProcessComponentConsumer pair : funcs) {
                pair.accept(process, source);
            }
        });
        processContext.setLock((lock) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            processRepository.lock(process, lock);
        });
        processContext.process = this;

        return processContext;
    }

    public synchronized ProcessHandleTableObject getHandleTable() {
        ProcessEntity process = this.getProcess();

        ProcessHandleTableObject processHandleTable = this.factoryManager.create(ProcessHandleTableObject.class);

        processHandleTable.setSource(() -> {
            Set<ReadProcessComponentFunction> funcs = this.processorMediator.getReadProcessHandleTables();

            byte[] source = null;

            for (ReadProcessComponentFunction pair : funcs) {
                source = pair.apply(source, process);
            }

            return source;
        }, (byte[] source) -> {
            Set<WriteProcessComponentConsumer> funcs = this.processorMediator.getWriteProcessHandleTables();

            for (WriteProcessComponentConsumer pair : funcs) {
                pair.accept(process, source);
            }
        });
        processHandleTable.setLock((lock) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            processRepository.lock(process, lock);
        });
        processHandleTable.process = this;

        return processHandleTable;
    }

    public synchronized ProcessSessionObject getSession() {
        ProcessEntity process = this.getProcess();

        ProcessSessionObject processSession = this.factoryManager.create(ProcessSessionObject.class);

        processSession.setSource(() -> {
            return process;
        }, (processEntity -> {
        }));
        processSession.setLock((lock) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            processRepository.lock(process, lock);
        });
        processSession.process = this;
        return processSession;
    }

    public synchronized ProcessStatisticsObject getStatistics() {
        ProcessEntity process = this.getProcess();

        ProcessStatisticsObject processStatistics = this.factoryManager.create(ProcessStatisticsObject.class);

        processStatistics.setSource(() -> {
            Set<ReadProcessComponentFunction> funcs = this.processorMediator.getReadProcessStatistics();

            byte[] source = null;

            for (ReadProcessComponentFunction pair : funcs) {
                source = pair.apply(source, process);
            }

            return source;
        }, (byte[] source) -> {
            Set<WriteProcessComponentConsumer> funcs = this.processorMediator.getWriteProcessStatistics();

            for (WriteProcessComponentConsumer pair : funcs) {
                pair.accept(process, source);
            }
        });
        processStatistics.setLock((lock) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            processRepository.lock(process, lock);
        });
        processStatistics.process = this;

        return processStatistics;
    }

    public synchronized ProcessTokenObject getToken() {
        ProcessEntity process = this.getProcess();

        ProcessTokenObject processToken = this.factoryManager.create(ProcessTokenObject.class);

        processToken.setSource(() -> {
            Set<ReadProcessComponentFunction> funcs = this.processorMediator.getReadProcessTokens();

            byte[] source = null;

            for (ReadProcessComponentFunction pair : funcs) {
                source = pair.apply(source, process);
            }

            return source;
        }, (byte[] source) -> {
            Set<WriteProcessComponentConsumer> funcs = this.processorMediator.getWriteProcessTokens();

            for (WriteProcessComponentConsumer pair : funcs) {
                pair.accept(process, source);
            }
        });
        processToken.setLock((lock) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            processRepository.lock(process, lock);
        });
        processToken.process = this;

        return processToken;
    }
}
