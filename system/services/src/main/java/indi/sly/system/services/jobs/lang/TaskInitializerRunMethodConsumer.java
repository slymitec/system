package indi.sly.system.services.jobs.lang;

import indi.sly.system.common.lang.Consumer1;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;

@FunctionalInterface
public interface TaskInitializerRunMethodConsumer extends Consumer1<TaskContentObject> {
}
