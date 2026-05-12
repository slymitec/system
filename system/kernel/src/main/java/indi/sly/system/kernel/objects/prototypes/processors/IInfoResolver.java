package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.IOrderlyResolver;
import indi.sly.system.kernel.objects.prototypes.mediators.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;

public interface IInfoResolver extends IOrderlyResolver {
    void resolve(InfoEntity info, InfoProcessorMediator processorMediator);
}

/*
InfoSelfResolver                        0
InfoParentResolver		            	0
InfoCheckConditionResolver              0
InfoProcessInfoTableResolver            1
InfoOpenOrCloseResolver		    	    1
InfoSecurityDescriptorResolver	    	1
InfoDumpResolver                        2
InfoTypeInitializerResolver	            2
InfoProcessInfoTableCloseResolver       3
InfoDateResolver                        3
InfoSecurityDescriptorCreateResolver  	3
InfoCloseThenDeleteIfTemporaryResolver  4
InfoProcessAndThreadStatisticsResolver  4
 */