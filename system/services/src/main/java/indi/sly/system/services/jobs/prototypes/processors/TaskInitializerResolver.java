package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.common.lang.ASystemException;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.services.core.environment.values.ServiceKernelExtensionSpaceDefinition;
import indi.sly.system.services.core.prototypes.TransactionalActionComponent;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskProcessorFinishConsumer;
import indi.sly.system.services.jobs.lang.TaskProcessorRunConsumer;
import indi.sly.system.services.jobs.lang.TaskProcessorStartConsumer;
import indi.sly.system.services.jobs.prototypes.mediators.TaskProcessorMediator;
import indi.sly.system.services.jobs.values.TaskAttributeType;
import indi.sly.system.services.jobs.values.TaskDefinition;
import indi.sly.system.services.jobs.values.TaskInitializerRunRecord;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskInitializerResolver extends AResolver implements ITaskResolver {
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
            TaskInitializerRunRecord initializerRun = initializer.getRun(name);

            try {
                long initializerRunTransaction = TransactionType.WHATEVER;
                if (!LogicalUtil.isAnyExist(task.getAttribute(), TaskAttributeType.HAS_NOT_TRANSACTION)) {
                    initializerRunTransaction = initializerRun.transaction();
                }

                Provider<Void> provider = () -> {
                    initializerRun.method().accept(run, content);

                    return null;
                };

                ServiceKernelExtensionSpaceDefinition serviceSpace = (ServiceKernelExtensionSpaceDefinition) this.coreManager.getKernelSpace().getServiceSpace();
                TransactionalActionComponent transactionalAction = serviceSpace.getTransactionalAction();

                if (LogicalUtil.isAnyEqual(initializerRunTransaction, TransactionType.INDEPENDENCE)) {
                    transactionalAction.runWithIndependentTransactional(provider);
                } else if (LogicalUtil.isAnyEqual(initializerRunTransaction, TransactionType.MIX)) {
                    transactionalAction.runWithTransactional(provider);
                } else if (LogicalUtil.isAnyEqual(initializerRunTransaction, TransactionType.PROHIBITED)) {
                    transactionalAction.runWithoutTransactional(provider);
                } else if (LogicalUtil.isAnyEqual(initializerRunTransaction, TransactionType.WHATEVER)) {
                    transactionalAction.runWithWhatever(provider);
                }
            } catch (ASystemException exception) {
                content.setException(exception);
            }
        };
    }

    @Override
    public int order() {
        return 2;
    }

    private final TaskProcessorStartConsumer start;
    private final TaskProcessorFinishConsumer finish;
    private final TaskProcessorRunConsumer run;

    @Override
    public void resolve(TaskDefinition task, TaskProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
        processorMediator.getFinishes().add(this.finish);
        processorMediator.getRuns().add(this.run);
    }
}
