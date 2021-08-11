package indi.sly.system.services.job.lang;

import indi.sly.system.common.lang.Consumer2;
import indi.sly.system.services.job.prototypes.JobContentObject;

@FunctionalInterface
public interface JobInitializerRunMethodConsumer extends Consumer2<JobRunConsumer, JobContentObject> {
}
