package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessEntity;

public interface IProcessCreaterResolver {
    void resolve(ProcessEntity process, ProcessProcessorMediator processorMediator);
}
