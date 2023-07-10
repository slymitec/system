package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.services.jobs.prototypes.wrappers.UserContextProcessorMediator;

public abstract class AUserContextFinishResolver extends AResolver {
    public abstract void resolve(UserContextProcessorMediator processorMediator);
}
