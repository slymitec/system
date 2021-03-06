package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;

public interface IProcessResolver {
    void resolve(ProcessEntity process, ProcessProcessorMediator processorMediator);
}
