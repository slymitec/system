package indi.sly.system.services.job.lang;

import indi.sly.system.common.lang.Consumer2;
import indi.sly.system.services.job.prototypes.TaskContentObject;

@FunctionalInterface
public interface TaskInitializerRunMethodConsumer extends Consumer2<TaskRunConsumer, TaskContentObject> {
}
