package indi.sly.system.services.jobs.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.ABuilder;
import indi.sly.system.services.jobs.lang.UserContextProcessorCreateFunction;
import indi.sly.system.services.jobs.prototypes.wrappers.UserContextProcessorMediator;
import indi.sly.system.services.jobs.values.UserContextDefinition;
import indi.sly.system.services.jobs.values.UserContextRequestDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextCreateBuilder extends ABuilder {
    protected JobFactory factory;
    protected UserContextProcessorMediator processorMediator;

    public UserContextObject create(UserContextRequestDefinition userContextRequest) {
        if (ValueUtil.isAnyNullOrEmpty(userContextRequest)) {
            throw new ConditionParametersException();
        }

        UserContextDefinition userContext = new UserContextDefinition();

        List<UserContextProcessorCreateFunction> resolvers = this.processorMediator.getCreates();

        for (UserContextProcessorCreateFunction resolver : resolvers) {
            userContext = resolver.apply(userContext, userContextRequest);
        }

        return this.factory.buildUserContext(userContext);
    }
}
