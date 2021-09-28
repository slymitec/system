package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.services.job.prototypes.wrappers.TaskProcessorMediator;
import indi.sly.system.services.job.values.TaskDefinition;

public abstract class ATaskResolver extends AResolver {
    public abstract void resolve(TaskDefinition task, TaskProcessorMediator processorMediator);
}
