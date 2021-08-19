package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.services.job.prototypes.wrappers.JobProcessorMediator;
import indi.sly.system.services.job.values.JobDefinition;

public abstract class AJobResolver extends AResolver {
    public abstract void resolve(JobDefinition job, JobProcessorMediator processorMediator);
}
