package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessEntity;

public abstract class AProcessResolver extends AResolver {
    public abstract void resolve(ProcessEntity process, ProcessProcessorMediator processorMediator);
}
