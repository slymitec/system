package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.IOrderlyResolver;
import indi.sly.system.services.jobs.prototypes.mediators.TaskProcessorMediator;
import indi.sly.system.services.jobs.values.TaskDefinition;

public interface ITaskResolver extends IOrderlyResolver {
    void resolve(TaskDefinition task, TaskProcessorMediator processorMediator);
}
