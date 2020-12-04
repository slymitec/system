package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.ProcessProcessorRegister;

public interface IProcessProcessor {
    void process(ProcessEntity process, ProcessProcessorRegister processorRegister);
}
