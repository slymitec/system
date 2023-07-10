package indi.sly.system.services.jobs.lang;

import indi.sly.system.common.lang.Consumer2;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;

@FunctionalInterface
public interface TaskInitializerRunMethodConsumer extends Consumer2<TaskRunConsumer, TaskContentObject> {
}
