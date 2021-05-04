package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;

public interface IInfoResolver {
    void process(InfoEntity info, InfoProcessorMediator processorRegister);
}
