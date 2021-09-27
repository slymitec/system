package indi.sly.system.services.auxiliary.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.ABuilder;
import indi.sly.system.services.auxiliary.lang.UserContextProcessorFinishFunction;
import indi.sly.system.services.auxiliary.prototypes.wrappers.AuxiliaryProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextFinishBuilder extends ABuilder {
    protected AuxiliaryFactory factory;
    protected AuxiliaryProcessorMediator processorMediator;

    public void build(UserContextObject userContext) {
        if (ObjectUtil.isAnyNull(userContext)) {
            throw new ConditionParametersException();
        }

        List<UserContextProcessorFinishFunction> resolvers = this.processorMediator.getFinishes();

        for (UserContextProcessorFinishFunction resolver : resolvers) {
            userContext = resolver.apply(userContext);
        }
    }
}
