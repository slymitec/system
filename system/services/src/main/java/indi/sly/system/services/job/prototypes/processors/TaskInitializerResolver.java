package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.services.core.prototypes.TransactionalActionObject;
import indi.sly.system.services.job.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.job.lang.*;
import indi.sly.system.services.job.prototypes.TaskContentObject;
import indi.sly.system.services.job.prototypes.wrappers.TaskProcessorMediator;
import indi.sly.system.services.job.values.TaskAttributeType;
import indi.sly.system.services.job.values.TaskDefinition;
import indi.sly.system.services.job.values.TaskInitializerRunSummaryDefinition;
import indi.sly.system.services.core.values.TransactionType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskInitializerResolver extends ATaskResolver {
    public TaskInitializerResolver() {
        this.start = (task, status) -> {
            ATaskInitializer initializer = task.getInitializer();

            initializer.start(task);
        };

        this.finish = (task, status) -> {
            ATaskInitializer initializer = task.getInitializer();

            initializer.finish(task);
        };

        this.run = (task, status, name, run, content) -> {
            ATaskInitializer initializer = task.getInitializer();
            TaskInitializerRunSummaryDefinition initializerRun = initializer.getRun(name);

            try {
                long initializerRunTransaction = TransactionType.WHATEVER;
                if (!LogicalUtil.isAnyExist(task.getAttribute(), TaskAttributeType.HAS_NOT_TRANSACTION)) {
                    initializerRunTransaction = initializerRun.getTransaction();
                }

                TransactionalActionObject transactionalAction = this.factoryManager.create(TransactionalActionObject.class);
                transactionalAction.run(initializerRunTransaction, (Provider<Void>) () -> {
                    initializerRun.getMethod().accept(run, content);

                    return null;
                });
            } catch (AKernelException exception) {
                content.setException(exception);
            }
        };
    }

    @Override
    public int order() {
        return 3;
    }

    private final TaskProcessorStartFunction start;
    private final TaskProcessorFinishConsumer finish;
    private final TaskProcessorRunConsumer run;

    @Transactional(value = Transactional.TxType.SUPPORTS)
    protected void runEntry(TaskInitializerRunMethodConsumer initializerRunMethodEntry,
                            TaskRunConsumer run, TaskContentObject content) {
        initializerRunMethodEntry.accept(run, content);
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    protected void runEntryWithIndependentTransactional(TaskInitializerRunMethodConsumer initializerRunMethodEntry,
                                                        TaskRunConsumer run, TaskContentObject content) {
        initializerRunMethodEntry.accept(run, content);
    }

    @Transactional(value = Transactional.TxType.NOT_SUPPORTED)
    protected void runEntryWithoutTransactional(TaskInitializerRunMethodConsumer initializerRunMethodEntry,
                                                TaskRunConsumer run, TaskContentObject content) {
        initializerRunMethodEntry.accept(run, content);
    }

    @Override
    public void resolve(TaskDefinition task, TaskProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
        processorMediator.getFinishes().add(this.finish);
        processorMediator.getRuns().add(this.run);
    }
}
