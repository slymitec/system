package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.services.jobs.lang.TaskProcessorFinishConsumer;
import indi.sly.system.services.jobs.lang.TaskProcessorStartConsumer;
import indi.sly.system.services.jobs.prototypes.wrappers.TaskProcessorMediator;
import indi.sly.system.services.jobs.values.TaskDefinition;
import indi.sly.system.services.jobs.values.TaskStatusRuntimeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskStatusRuntimeResolver extends ATaskResolver {
    public TaskStatusRuntimeResolver() {
        this.start = (task, status) -> status.setRuntime(TaskStatusRuntimeType.RUNNING);

        this.finish = (task, status) -> status.setRuntime(TaskStatusRuntimeType.FINISHED);
    }

    @Override
    public int order() {
        return 4;
    }

    private final TaskProcessorStartConsumer start;
    private final TaskProcessorFinishConsumer finish;

    @Override
    public void resolve(TaskDefinition task, TaskProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
        processorMediator.getFinishes().add(this.finish);
    }
}
