package indi.sly.system.services.auxiliary.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.Consumer1;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.services.auxiliary.prototypes.processors.AUserContextResolver;
import indi.sly.system.services.auxiliary.prototypes.wrappers.AuxiliaryProcessorMediator;
import indi.sly.system.services.auxiliary.values.UserContextDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuxiliaryFactory extends AFactory {
    protected List<AUserContextResolver> userContextResolvers;

    @Override
    public void init() {
        this.userContextResolvers = new CopyOnWriteArrayList<>();

        Collections.sort(this.userContextResolvers);
    }

    private UserContextObject buildUserContext(AuxiliaryProcessorMediator processorMediator, Provider<UserContextDefinition> funcRead,
                                               Consumer1<UserContextDefinition> funcWrite) {
        UserContextObject userContext = this.factoryManager.create(UserContextObject.class);

        userContext.setSource(funcRead, funcWrite);
        userContext.processorMediator = processorMediator;

        return userContext;
    }


    public UserContextObject buildUserContext(UserContextDefinition userContext) {
        if (ObjectUtil.isAnyNull(userContext)) {
            throw new ConditionParametersException();
        }

        AuxiliaryProcessorMediator processorMediator = this.factoryManager.create(AuxiliaryProcessorMediator.class);

        for (AUserContextResolver userContextResolver : this.userContextResolvers) {
            userContextResolver.resolve(userContext, processorMediator);
        }

        return this.buildUserContext(processorMediator, () -> userContext, (source) -> {
        });
    }

    public UserContextBuilder createUserContext() {
        UserContextBuilder userContextBuilder = this.factoryManager.create(UserContextBuilder.class);

        userContextBuilder.factory = this;

        return userContextBuilder;
    }
}
