package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.prototypes.InfoProcessorRegister;

public interface IInfoObjectProcessor {
    void process(InfoEntity info, InfoProcessorRegister processorRegister);
}
