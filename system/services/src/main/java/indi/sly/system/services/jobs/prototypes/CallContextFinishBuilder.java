package indi.sly.system.services.jobs.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.ABuilder;
import indi.sly.system.services.jobs.lang.CallContextProcessorEndFunction;
import indi.sly.system.services.jobs.prototypes.mediators.CallContextProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CallContextFinishBuilder extends ABuilder {
    protected JobFactory factory;
    protected CallContextProcessorMediator processorMediator;

    public void finish(CallContextObject userContext) {
        if (ObjectUtil.isAnyNull(userContext)) {
            throw new ConditionParametersException();
        }

        List<CallContextProcessorEndFunction> resolvers = this.processorMediator.getEnds();

        for (CallContextProcessorEndFunction resolver : resolvers) {
            userContext = resolver.apply(userContext);
        }
    }
}
