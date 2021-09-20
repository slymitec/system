package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AInfoResolver extends AResolver {
    public abstract void resolve(InfoEntity info, InfoProcessorMediator processorMediator);
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
InfoProcessAndThreadStatisticsResolver  3
InfoDateResolver                        3
InfoSecurityDescriptorCreateResolver  	3
 */