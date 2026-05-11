package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.IOrderlyResolver;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;

public interface IProcessEndResolver extends IOrderlyResolver {
    void resolve(ProcessLifeProcessorMediator processorCreatorMediator);
}

/*
ProcessEndCommunicationResolver 1
ProcessEndSessionResolver       1
ProcessEndInfoTableResolver     2
ProcessEndNotifyParentResolver  3
 */