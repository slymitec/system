package indi.sly.system.services.job.lang;

import indi.sly.system.common.lang.Consumer2;
import indi.sly.system.services.job.values.JobDefinition;
import indi.sly.system.services.job.values.JobStatusDefinition;

@FunctionalInterface
public interface JobProcessorFinishConsumer extends Consumer2<JobDefinition, JobStatusDefinition> {
}
