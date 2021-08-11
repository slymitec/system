package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.IOrderlyResolver;
import indi.sly.system.services.job.prototypes.wrappers.JobProcessorMediator;
import indi.sly.system.services.job.values.JobDefinition;

public interface IJobResolver extends IOrderlyResolver {
    void resolve(JobDefinition job, JobProcessorMediator processorMediator);
}
