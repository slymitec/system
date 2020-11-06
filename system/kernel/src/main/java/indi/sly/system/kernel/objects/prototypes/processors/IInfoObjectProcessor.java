package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.prototypes.InfoObjectProcessorRegister;

public interface IInfoObjectProcessor {
    void process(InfoEntity info, InfoObjectProcessorRegister processorRegister);
}
