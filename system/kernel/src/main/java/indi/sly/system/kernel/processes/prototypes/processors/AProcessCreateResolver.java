package indi.sly.system.kernel.processes.prototypes.processors;


import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AProcessCreateResolver extends AResolver {
    public abstract void resolve(ProcessLifeProcessorMediator processorCreatorMediator);
}

/*
ProcessCreateCheckResolver		    0
ProcessCreateSessionResolver		1
ProcessCreateStatisticsResolver		1
ProcessCreateTokenResolver	    	1
ProcessCreateContextResolver		2
ProcessCreateInfoTableResolver		3
ProcessCreateNotifyParentResolver	4
 */
