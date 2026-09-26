package indi.sly.system.services.jobs.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.ABuilder;
import indi.sly.system.services.jobs.lang.CallContextProcessorCreateFunction;
import indi.sly.system.services.jobs.prototypes.mediators.CallContextProcessorMediator;
import indi.sly.system.services.jobs.values.CallContextDefinition;
import indi.sly.system.services.jobs.values.CallContextRecord;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CallContextCreateBuilder extends ABuilder {
    protected JobFactory factory;
    protected CallContextProcessorMediator processorMediator;

    public CallContextObject create(CallContextRecord callContext) {
        if (ValueUtil.isAnyNullOrEmpty(callContext)) {
            throw new ConditionParametersException();
        }

        CallContextDefinition userContext = new CallContextDefinition();

        List<CallContextProcessorCreateFunction> resolvers = this.processorMediator.getCreates();

        for (CallContextProcessorCreateFunction resolver : resolvers) {
            userContext = resolver.apply(userContext, callContext);
        }

        return this.factory.buildUserContext(userContext);
    }
}
