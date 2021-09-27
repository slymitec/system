package indi.sly.system.services.auxiliary.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.services.auxiliary.prototypes.wrappers.AuxiliaryPorcessorMediator;

public abstract class AAuxiliaryResolver extends AResolver {
    public abstract void resolve(AuxiliaryPorcessorMediator porcessorMediator);
}
