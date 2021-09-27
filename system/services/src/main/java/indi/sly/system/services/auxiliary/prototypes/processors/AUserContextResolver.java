package indi.sly.system.services.auxiliary.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.services.auxiliary.prototypes.wrappers.AuxiliaryProcessorMediator;
import indi.sly.system.services.auxiliary.values.UserContextDefinition;

public abstract class AUserContextResolver extends AResolver {
    public abstract void resolve(UserContextDefinition userContext, AuxiliaryProcessorMediator porcessorMediator);
}
