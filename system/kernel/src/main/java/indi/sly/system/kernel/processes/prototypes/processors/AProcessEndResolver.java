package indi.sly.system.kernel.processes.prototypes.processors;


import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AProcessEndResolver extends AResolver {
    public abstract void resolve(ProcessLifeProcessorMediator processorCreatorMediator);
}

/*
ProcessEndInfoTableResolver     1
ProcessEndCommunicationResolver 2
ProcessEndNotifyParentResolver  3
 */