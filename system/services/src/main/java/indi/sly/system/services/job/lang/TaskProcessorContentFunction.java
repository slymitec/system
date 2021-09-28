package indi.sly.system.services.job.lang;

import indi.sly.system.common.lang.Function3;
import indi.sly.system.kernel.processes.prototypes.ThreadContextObject;
import indi.sly.system.services.job.values.TaskDefinition;
import indi.sly.system.services.job.values.TaskStatusDefinition;

@FunctionalInterface
public interface TaskProcessorContentFunction extends Function3<ThreadContextObject, TaskDefinition, TaskStatusDefinition,
        ThreadContextObject> {
}
