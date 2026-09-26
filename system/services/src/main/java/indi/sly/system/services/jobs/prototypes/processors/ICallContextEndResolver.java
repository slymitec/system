package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.IOrderlyResolver;
import indi.sly.system.services.jobs.prototypes.mediators.CallContextProcessorMediator;

public interface ICallContextEndResolver extends IOrderlyResolver {
    void resolve(CallContextProcessorMediator processorMediator);
}
