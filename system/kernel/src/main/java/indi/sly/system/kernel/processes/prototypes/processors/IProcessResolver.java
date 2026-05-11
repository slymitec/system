package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.IOrderlyResolver;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessEntity;

public interface IProcessResolver extends IOrderlyResolver {
    void resolve(ProcessEntity process, ProcessProcessorMediator processorMediator);
}
