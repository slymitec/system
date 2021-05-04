package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;

public interface IInfoResolver {
    void resolve(InfoEntity info, InfoProcessorMediator processorMediator);
}
