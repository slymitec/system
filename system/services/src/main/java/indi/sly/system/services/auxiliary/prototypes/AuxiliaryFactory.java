package indi.sly.system.services.auxiliary.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.Consumer1;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.services.auxiliary.prototypes.processors.AUserContextCreatetesolver;
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
    protected List<AUserContextCreatetesolver> userContextCreateResolvers;

    @Override
    public void init() {
        this.userContextCreateResolvers = new CopyOnWriteArrayList<>();

        Collections.sort(this.userContextCreateResolvers);
    }

    private UserContextObject buildUserContext(Provider<UserContextDefinition> funcRead, Consumer1<UserContextDefinition> funcWrite) {
        UserContextObject userContext = this.factoryManager.create(UserContextObject.class);

        userContext.setSource(funcRead, funcWrite);

        return userContext;
    }

    public UserContextObject buildUserContext(UserContextDefinition userContext) {
        if (ObjectUtil.isAnyNull(userContext)) {
            throw new ConditionParametersException();
        }

        return this.buildUserContext(() -> userContext, (source) -> {
        });
    }

    public UserContextBuilder createUserContext() {
        AuxiliaryProcessorMediator processorMediator = this.factoryManager.create(AuxiliaryProcessorMediator.class);

        for (AUserContextCreatetesolver userContextResolver : this.userContextCreateResolvers) {
            userContextResolver.resolve(processorMediator);
        }

        UserContextBuilder userContextBuilder = this.factoryManager.create(UserContextBuilder.class);

        userContextBuilder.processorMediator = processorMediator;
        userContextBuilder.factory = this;

        return userContextBuilder;
    }
}
