package indi.sly.system.services.job.lang;

import indi.sly.system.common.lang.Consumer2;
import indi.sly.system.services.job.values.TaskDefinition;
import indi.sly.system.services.job.values.TaskStatusDefinition;

@FunctionalInterface
public interface TaskProcessorFinishConsumer extends Consumer2<TaskDefinition, TaskStatusDefinition> {
}
