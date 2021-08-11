package indi.sly.system.services.job.lang;

import indi.sly.system.common.lang.Function3;
import indi.sly.system.kernel.processes.prototypes.ThreadContextObject;
import indi.sly.system.services.job.values.JobDefinition;
import indi.sly.system.services.job.values.JobStatusDefinition;

@FunctionalInterface
public interface JobProcessorContentFunction extends Function3<ThreadContextObject, JobDefinition, JobStatusDefinition,
        ThreadContextObject> {
}
