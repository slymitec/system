package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.kernel.processes.entities.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.ProcessObjectProcessorRegister;

public interface IProcessObjectProcessor {
    void postProcess(ProcessEntity process, ProcessObjectProcessorRegister processorRegister);
}
