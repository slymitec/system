package indi.sly.system.services.jobs.lang;

import indi.sly.system.common.lang.Consumer4;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.TaskDefinition;
import indi.sly.system.services.jobs.values.TaskStatusDefinition;

@FunctionalInterface
public interface TaskProcessorRunConsumer extends Consumer4<TaskDefinition, TaskStatusDefinition, String, TaskContentObject> {
}
