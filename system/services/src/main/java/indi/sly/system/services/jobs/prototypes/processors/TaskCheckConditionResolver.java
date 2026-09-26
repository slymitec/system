package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.services.jobs.lang.*;
import indi.sly.system.services.jobs.prototypes.mediators.TaskProcessorMediator;
import indi.sly.system.services.jobs.values.TaskDefinition;
import indi.sly.system.services.jobs.values.TaskStatusRuntimeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskCheckConditionResolver extends AResolver implements ITaskResolver {
    public TaskCheckConditionResolver() {
        this.start = (task, status) -> {
            if (status.getRuntime() != TaskStatusRuntimeType.INITIALIZATION) {
                throw new StatusRelationshipErrorException();
            }
        };

        this.end = (task, status) -> {
            if (status.getRuntime() != TaskStatusRuntimeType.RUNNING) {
                throw new StatusRelationshipErrorException();
            }
        };

        this.run = (task, status, name, content) -> {
            if (status.getRuntime() != TaskStatusRuntimeType.RUNNING) {
                throw new StatusRelationshipErrorException();
            }
        };

        this.finish = (task, status, name, content) -> {
            if (status.getRuntime() != TaskStatusRuntimeType.RUNNING) {
                throw new StatusRelationshipErrorException();
            }
        };

        this.content = (task, status, threadContext) -> {
            if (status.getRuntime() != TaskStatusRuntimeType.RUNNING) {
                throw new StatusRelationshipErrorException();
            }

            return threadContext;
        };
    }

    @Override
    public int order() {
        return 0;
    }

    private final TaskProcessorStartConsumer start;
    private final TaskProcessorEndConsumer end;
    private final TaskProcessorRunConsumer run;
    private final TaskProcessorFinishConsumer finish;
    private final TaskProcessorContentFunction content;

    @Override
    public void resolve(TaskDefinition task, TaskProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
        processorMediator.getEnds().add(this.end);
        processorMediator.getRuns().add(this.run);
        processorMediator.getFinishes().add(this.finish);
        processorMediator.getContents().add(this.content);
    }
}
