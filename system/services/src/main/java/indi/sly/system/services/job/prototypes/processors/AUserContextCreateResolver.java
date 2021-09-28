package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.services.job.prototypes.wrappers.UserContextProcessorMediator;

public abstract class AUserContextCreateResolver extends AResolver {
    public abstract void resolve(UserContextProcessorMediator processorMediator);
}
