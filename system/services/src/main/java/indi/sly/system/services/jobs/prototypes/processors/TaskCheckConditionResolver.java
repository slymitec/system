package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.services.jobs.lang.TaskProcessorContentFunction;
import indi.sly.system.services.jobs.lang.TaskProcessorFinishConsumer;
import indi.sly.system.services.jobs.lang.TaskProcessorRunConsumer;
import indi.sly.system.services.jobs.lang.TaskProcessorStartConsumer;
import indi.sly.system.services.jobs.prototypes.wrappers.TaskProcessorMediator;
import indi.sly.system.services.jobs.values.TaskDefinition;
import indi.sly.system.services.jobs.values.TaskStatusRuntimeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskCheckConditionResolver extends ATaskResolver {
    public TaskCheckConditionResolver() {
        this.start = (task, status) -> {
            if (status.getRuntime() != TaskStatusRuntimeType.INITIALIZATION) {
                throw new StatusRelationshipErrorException();
            }
        };

        this.finish = (task, status) -> {
            if (status.getRuntime() != TaskStatusRuntimeType.RUNNING) {
                throw new StatusRelationshipErrorException();
            }
        };

        this.run = (task, status, name, run, content) -> {
            if (status.getRuntime() != TaskStatusRuntimeType.RUNNING) {
                throw new StatusRelationshipErrorException();
            }
        };

        this.content = (task, status, threadRun) -> {
            if (status.getRuntime() != TaskStatusRuntimeType.RUNNING) {
                throw new StatusRelationshipErrorException();
            }

            return threadRun;
        };
    }

    @Override
    public int order() {
        return 0;
    }

    private final TaskProcessorStartConsumer start;
    private final TaskProcessorFinishConsumer finish;
    private final TaskProcessorRunConsumer run;
    private final TaskProcessorContentFunction content;

    @Override
    public void resolve(TaskDefinition task, TaskProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
        processorMediator.getFinishes().add(this.finish);
        processorMediator.getRuns().add(this.run);
        processorMediator.getContents().add(this.content);
    }
}
