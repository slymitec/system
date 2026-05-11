package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.IOrderlyResolver;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;

public interface IProcessCreateResolver extends IOrderlyResolver {
    void resolve(ProcessLifeProcessorMediator processorCreatorMediator);
}

/*
ProcessCreateCheckResolver		    0
ProcessCreateStatisticsResolver		1
ProcessCreateTokenResolver	    	1
ProcessCreateSessionResolver		2
ProcessCreateContextResolver		3
ProcessCreateInfoTableResolver		4
ProcessCreateTokenRuleResolver      4
ProcessCreateNotifyParentResolver	5
 */
