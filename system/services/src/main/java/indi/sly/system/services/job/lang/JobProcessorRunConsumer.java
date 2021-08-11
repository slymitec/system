package indi.sly.system.services.job.lang;

import indi.sly.system.common.lang.Consumer5;
import indi.sly.system.services.job.prototypes.JobContentObject;
import indi.sly.system.services.job.values.JobDefinition;
import indi.sly.system.services.job.values.JobStatusDefinition;

@FunctionalInterface
public interface JobProcessorRunConsumer extends Consumer5<JobDefinition, JobStatusDefinition, String,
        JobRunConsumer, JobContentObject> {
}
