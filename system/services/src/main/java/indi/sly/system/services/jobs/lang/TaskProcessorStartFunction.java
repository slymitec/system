package indi.sly.system.services.jobs.lang;

import indi.sly.system.common.lang.Consumer2;
import indi.sly.system.services.jobs.values.TaskDefinition;
import indi.sly.system.services.jobs.values.TaskStatusDefinition;

@FunctionalInterface
public interface TaskProcessorStartFunction extends Consumer2<TaskDefinition, TaskStatusDefinition> {
}
