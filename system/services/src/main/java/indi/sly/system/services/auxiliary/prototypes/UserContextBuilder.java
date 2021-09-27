package indi.sly.system.services.auxiliary.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.ABuilder;
import indi.sly.system.services.auxiliary.lang.UserContextProcessorCreateFunction;
import indi.sly.system.services.auxiliary.prototypes.wrappers.AuxiliaryProcessorMediator;
import indi.sly.system.services.auxiliary.values.UserContextDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextBuilder extends ABuilder {
    protected AuxiliaryFactory factory;
    protected AuxiliaryProcessorMediator processorMediator;

    public UserContextObject create(Map<String, String> userRequest) {
        if (ValueUtil.isAnyNullOrEmpty(userRequest)) {
            throw new ConditionParametersException();
        }

        UserContextDefinition userContext = new UserContextDefinition();

        List<UserContextProcessorCreateFunction> resolvers = this.processorMediator.getCreates();

        for (UserContextProcessorCreateFunction resolver : resolvers) {
            userContext = resolver.apply(userContext, userRequest);
        }

        return this.factory.buildUserContext(userContext);
    }
}
