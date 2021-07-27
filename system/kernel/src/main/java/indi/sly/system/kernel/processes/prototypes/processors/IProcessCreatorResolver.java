package indi.sly.system.kernel.processes.prototypes.processors;


import indi.sly.system.kernel.core.prototypes.processors.IOrderlyResolver;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessCreatorProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessCreatorDefinition;

public interface IProcessCreatorResolver extends IOrderlyResolver {
    void resolve(ProcessCreatorDefinition processCreator, ProcessCreatorProcessorMediator processorCreatorMediator);
}
