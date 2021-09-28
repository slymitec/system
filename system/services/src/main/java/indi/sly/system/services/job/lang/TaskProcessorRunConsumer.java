package indi.sly.system.services.job.lang;

import indi.sly.system.common.lang.Consumer5;
import indi.sly.system.services.job.prototypes.TaskContentObject;
import indi.sly.system.services.job.values.TaskDefinition;
import indi.sly.system.services.job.values.TaskStatusDefinition;

@FunctionalInterface
public interface TaskProcessorRunConsumer extends Consumer5<TaskDefinition, TaskStatusDefinition, String,
        TaskRunConsumer, TaskContentObject> {
}
