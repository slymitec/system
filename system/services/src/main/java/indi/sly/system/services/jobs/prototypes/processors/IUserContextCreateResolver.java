package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.IOrderlyResolver;
import indi.sly.system.services.jobs.prototypes.mediators.UserContextProcessorMediator;

public interface IUserContextCreateResolver extends IOrderlyResolver {
    void resolve(UserContextProcessorMediator processorMediator);
}
