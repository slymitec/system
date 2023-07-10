package indi.sly.system.services.jobs.lang;

import indi.sly.system.common.lang.Consumer5;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.TaskDefinition;
import indi.sly.system.services.jobs.values.TaskStatusDefinition;

@FunctionalInterface
public interface TaskProcessorRunConsumer extends Consumer5<TaskDefinition, TaskStatusDefinition, String,
        TaskRunConsumer, TaskContentObject> {
}
