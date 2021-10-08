package indi.sly.system.services.job.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.ABuilder;
import indi.sly.system.services.job.lang.UserContextProcessorFinishFunction;
import indi.sly.system.services.job.prototypes.wrappers.UserContextProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextFinishBuilder extends ABuilder {
    protected JobFactory factory;
    protected UserContextProcessorMediator processorMediator;

    public void finish(UserContextObject userContext) {
        if (ObjectUtil.isAnyNull(userContext)) {
            throw new ConditionParametersException();
        }

        List<UserContextProcessorFinishFunction> resolvers = this.processorMediator.getFinishes();

        for (UserContextProcessorFinishFunction resolver : resolvers) {
            userContext = resolver.apply(userContext);
        }
    }
}
