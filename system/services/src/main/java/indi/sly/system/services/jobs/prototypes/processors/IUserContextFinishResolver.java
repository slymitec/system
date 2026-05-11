package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.IOrderlyResolver;
import indi.sly.system.services.jobs.prototypes.wrappers.UserContextProcessorMediator;

public interface IUserContextFinishResolver extends IOrderlyResolver {
    void resolve(UserContextProcessorMediator processorMediator);
}
