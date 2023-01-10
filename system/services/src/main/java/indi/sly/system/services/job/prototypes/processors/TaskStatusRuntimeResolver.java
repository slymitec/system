package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.services.job.lang.TaskProcessorFinishConsumer;
import indi.sly.system.services.job.lang.TaskProcessorStartFunction;
import indi.sly.system.services.job.prototypes.wrappers.TaskProcessorMediator;
import indi.sly.system.services.job.values.TaskDefinition;
import indi.sly.system.services.job.values.TaskStatusRuntimeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskStatusRuntimeResolver extends ATaskResolver {
    public TaskStatusRuntimeResolver() {
        this.start = (job, status) -> status.setRuntime(TaskStatusRuntimeType.RUNNING);

        this.finish = (job, status) -> status.setRuntime(TaskStatusRuntimeType.FINISHED);
    }

    @Override
    public int order() {
        return 4;
    }

    private final TaskProcessorStartFunction start;
    private final TaskProcessorFinishConsumer finish;

    @Override
    public void resolve(TaskDefinition task, TaskProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
        processorMediator.getFinishes().add(this.finish);
    }
}
