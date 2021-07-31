package indi.sly.system.services.center.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.IOrderlyResolver;
import indi.sly.system.services.center.prototypes.wrappers.CenterProcessorMediator;

public interface ICenterResolver extends IOrderlyResolver {
    void resolve(CenterProcessorMediator processorMediator);
}
