package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.services.core.prototypes.TransactionalActionObject;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.job.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.job.lang.TaskProcessorFinishConsumer;
import indi.sly.system.services.job.lang.TaskProcessorRunConsumer;
import indi.sly.system.services.job.lang.TaskProcessorStartFunction;
import indi.sly.system.services.job.prototypes.wrappers.TaskProcessorMediator;
import indi.sly.system.services.job.values.TaskAttributeType;
import indi.sly.system.services.job.values.TaskDefinition;
import indi.sly.system.services.job.values.TaskInitializerRunSummaryDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

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

                Provider<Void> provider = () -> {
                    initializerRun.getMethod().accept(run, content);

                    return null;
                };

                TransactionalActionObject transactionalAction = this.factoryManager.create(TransactionalActionObject.class);

                if (LogicalUtil.isAnyEqual(initializerRunTransaction, TransactionType.INDEPENDENCE)) {
                    transactionalAction.runWithIndependentTransactional(provider);
                } else if (LogicalUtil.isAnyEqual(initializerRunTransaction, TransactionType.MIX)) {
                    transactionalAction.runWithTransactional(provider);
                } else if (LogicalUtil.isAnyEqual(initializerRunTransaction, TransactionType.PROHIBITED)) {
                    transactionalAction.runWithoutTransactional(provider);
                } else if (LogicalUtil.isAnyEqual(initializerRunTransaction, TransactionType.WHATEVER)) {
                    transactionalAction.runWithWhatever(provider);
                }
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

    @Override
    public void resolve(TaskDefinition task, TaskProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
        processorMediator.getFinishes().add(this.finish);
        processorMediator.getRuns().add(this.run);
    }
}
