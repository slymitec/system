package indi.sly.system.kernel.processes.prototypes.processors;


import indi.sly.system.kernel.core.prototypes.processors.IOrderlyResolver;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessCreatorProcessorMediator;

public interface IProcessCreatorResolver extends IOrderlyResolver {
    void resolve(ProcessCreatorProcessorMediator processorCreatorMediator);
}
