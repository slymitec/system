package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.IOrderlyResolver;
import indi.sly.system.kernel.core.prototypes.processors.IResolver;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;

public interface IInfoResolver extends IOrderlyResolver {
    void resolve(InfoEntity info, InfoProcessorMediator processorMediator);
}
