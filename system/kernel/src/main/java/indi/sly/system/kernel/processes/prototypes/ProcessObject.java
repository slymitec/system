package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionContextException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.lang.ProcessProcessorReadComponentFunction;
import indi.sly.system.kernel.processes.lang.ProcessProcessorWriteComponentConsumer;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessObject extends AObject {
    protected ProcessFactory factory;
    protected ProcessProcessorMediator processorMediator;

    protected UUID id;

    public UUID getID() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.id;
    }

    public synchronized UUID getParentID() {
        return this.getSelf().getParentProcessID();
    }

    public synchronized boolean isCurrent() {
        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

        ThreadObject thread = threadManager.getCurrent();

        return this.id.equals(thread.getProcessID());
    }

    private synchronized ProcessEntity getSelf() {
        return this.processorMediator.getSelf().apply(this.id);
    }

    public synchronized ProcessStatusObject getStatus() {
        ProcessStatusObject processStatus = this.factoryManager.create(ProcessStatusObject.class);

        processStatus.setParent(this);
        processStatus.processorMediator = this.processorMediator;
        processStatus.setSource(this::getSelf, (ProcessEntity source) -> {
        });

        return processStatus;
    }

    public synchronized ProcessCommunicationObject getCommunication() {
        ProcessCommunicationObject processCommunication = this.factoryManager.create(ProcessCommunicationObject.class);

        processCommunication.setParent(this);
        processCommunication.setSource(() -> {
            ProcessEntity process = this.getSelf();

            Set<ProcessProcessorReadComponentFunction> methods = this.processorMediator.getReadProcessCommunications();

            byte[] source = null;

            for (ProcessProcessorReadComponentFunction method : methods) {
                source = method.apply(source, process);
            }

            return source;
        }, (byte[] source) -> {
            ProcessEntity process = this.getSelf();

            Set<ProcessProcessorWriteComponentConsumer> resolvers = this.processorMediator.getWriteProcessCommunications();

            for (ProcessProcessorWriteComponentConsumer resolver : resolvers) {
                resolver.accept(process, source);
            }
        });
        processCommunication.setLock((lock) -> {
            ProcessEntity process = this.getSelf();

            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            processRepository.lock(process, lock);
        });

        return processCommunication;
    }

    public synchronized ProcessContextObject getContext() {
        ProcessContextObject processContext = this.factoryManager.create(ProcessContextObject.class);

        processContext.setParent(this);
        processContext.setSource(() -> {
            ProcessEntity process = this.getSelf();

            Set<ProcessProcessorReadComponentFunction> resolvers = this.processorMediator.getReadProcessContexts();

            byte[] source = null;

            for (ProcessProcessorReadComponentFunction resolver : resolvers) {
                source = resolver.apply(source, process);
            }

            return source;
        }, (byte[] source) -> {
            ProcessEntity process = this.getSelf();

            Set<ProcessProcessorWriteComponentConsumer> resolvers = this.processorMediator.getWriteProcessContexts();

            for (ProcessProcessorWriteComponentConsumer resolver : resolvers) {
                resolver.accept(process, source);
            }
        });
        processContext.setLock((lock) -> {
            ProcessEntity process = this.getSelf();

            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            processRepository.lock(process, lock);
        });

        return processContext;
    }

    public synchronized ProcessInfoTableObject getInfoTable() {
        ProcessInfoTableObject processInfoTable = this.factoryManager.create(ProcessInfoTableObject.class);

        processInfoTable.setParent(this);
        processInfoTable.setSource(() -> {
            ProcessEntity process = this.getSelf();

            Set<ProcessProcessorReadComponentFunction> resolvers = this.processorMediator.getReadProcessInfoTables();

            byte[] source = null;

            for (ProcessProcessorReadComponentFunction resolver : resolvers) {
                source = resolver.apply(source, process);
            }

            return source;
        }, (byte[] source) -> {
            ProcessEntity process = this.getSelf();

            Set<ProcessProcessorWriteComponentConsumer> resolvers = this.processorMediator.getWriteProcessInfoTables();

            for (ProcessProcessorWriteComponentConsumer resolver : resolvers) {
                resolver.accept(process, source);
            }
        });
        processInfoTable.setLock((lock) -> {
            ProcessEntity process = this.getSelf();

            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            processRepository.lock(process, lock);
        });

        return processInfoTable;
    }

    public synchronized ProcessSessionObject getSession() {
        ProcessSessionObject processSession = this.factoryManager.create(ProcessSessionObject.class);

        processSession.setParent(this);
        processSession.setSource(() -> {
            ProcessEntity process = this.getSelf();

            Set<ProcessProcessorReadComponentFunction> resolvers = this.processorMediator.getReadProcessSessions();

            byte[] source = null;

            for (ProcessProcessorReadComponentFunction resolver : resolvers) {
                source = resolver.apply(source, process);
            }

            return source;
        }, (byte[] source) -> {
            ProcessEntity process = this.getSelf();

            Set<ProcessProcessorWriteComponentConsumer> resolvers = this.processorMediator.getWriteProcessSessions();

            for (ProcessProcessorWriteComponentConsumer resolver : resolvers) {
                resolver.accept(process, source);
            }
        });
        processSession.setLock((lock) -> {
            ProcessEntity process = this.getSelf();

            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            processRepository.lock(process, lock);
        });
        return processSession;
    }

    public synchronized ProcessStatisticsObject getStatistics() {
        ProcessStatisticsObject processStatistics = this.factoryManager.create(ProcessStatisticsObject.class);

        processStatistics.setParent(this);
        processStatistics.setSource(() -> {
            ProcessEntity process = this.getSelf();

            Set<ProcessProcessorReadComponentFunction> resolvers = this.processorMediator.getReadProcessStatistics();

            byte[] source = null;

            for (ProcessProcessorReadComponentFunction resolver : resolvers) {
                source = resolver.apply(source, process);
            }

            return source;
        }, (byte[] source) -> {
            ProcessEntity process = this.getSelf();

            Set<ProcessProcessorWriteComponentConsumer> resolvers = this.processorMediator.getWriteProcessStatistics();

            for (ProcessProcessorWriteComponentConsumer resolver : resolvers) {
                resolver.accept(process, source);
            }
        });
        processStatistics.setLock((lock) -> {
            ProcessEntity process = this.getSelf();

            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            processRepository.lock(process, lock);
        });

        return processStatistics;
    }

    public synchronized ProcessTokenObject getToken() {
        ProcessTokenObject processToken = this.factoryManager.create(ProcessTokenObject.class);

        processToken.setParent(this);
        processToken.setSource(() -> {
            ProcessEntity process = this.getSelf();

            Set<ProcessProcessorReadComponentFunction> resolvers = this.processorMediator.getReadProcessTokens();

            byte[] source = null;

            for (ProcessProcessorReadComponentFunction resolver : resolvers) {
                source = resolver.apply(source, process);
            }

            return source;
        }, (byte[] source) -> {
            ProcessEntity process = this.getSelf();

            Set<ProcessProcessorWriteComponentConsumer> resolvers = this.processorMediator.getWriteProcessTokens();

            for (ProcessProcessorWriteComponentConsumer resolver : resolvers) {
                resolver.accept(process, source);
            }
        });
        processToken.setLock((lock) -> {
            ProcessEntity process = this.getSelf();

            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            processRepository.lock(process, lock);
        });

        return processToken;
    }
}
