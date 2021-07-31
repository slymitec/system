package indi.sly.system.services.center.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.IOrderlyResolver;
import indi.sly.system.services.center.prototypes.wrappers.CenterProcessorMediator;
import indi.sly.system.services.center.values.CenterDefinition;

public interface ICenterResolver extends IOrderlyResolver {
    void resolve(CenterDefinition center, CenterProcessorMediator processorMediator);
}
