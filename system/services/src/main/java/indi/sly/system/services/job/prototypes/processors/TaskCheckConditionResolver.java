package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.services.job.lang.TaskProcessorContentFunction;
import indi.sly.system.services.job.lang.TaskProcessorFinishConsumer;
import indi.sly.system.services.job.lang.TaskProcessorRunConsumer;
import indi.sly.system.services.job.lang.TaskProcessorStartFunction;
import indi.sly.system.services.job.prototypes.wrappers.TaskProcessorMediator;
import indi.sly.system.services.job.values.TaskDefinition;
import indi.sly.system.services.job.values.TaskStatusRuntimeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskCheckConditionResolver extends ATaskResolver {
    public TaskCheckConditionResolver() {
        this.start = (job, status) -> {
            if (status.getRuntime() != TaskStatusRuntimeType.INITIALIZATION) {
                throw new StatusRelationshipErrorException();
            }
        };

        this.finish = (job, status) -> {
            if (status.getRuntime() != TaskStatusRuntimeType.RUNNING) {
                throw new StatusRelationshipErrorException();
            }
        };

        this.run = (job, status, name, run, content) -> {
            if (status.getRuntime() != TaskStatusRuntimeType.RUNNING) {
                throw new StatusRelationshipErrorException();
            }
        };

        this.content = (job, status, threadRun) -> {
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

    private final TaskProcessorStartFunction start;
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
