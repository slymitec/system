package indi.sly.system.kernel.processes.prototypes.processors;


import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;

public abstract class AProcessCreateResolver extends AResolver {
    public abstract void resolve(ProcessLifeProcessorMediator processorCreatorMediator);
}
