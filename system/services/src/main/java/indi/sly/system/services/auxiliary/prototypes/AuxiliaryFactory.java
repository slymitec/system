package indi.sly.system.services.auxiliary.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.Consumer1;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.services.auxiliary.prototypes.processors.*;
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
    public AuxiliaryFactory() {
        this.userContextCreateResolvers = new CopyOnWriteArrayList<>();
        this.userContextFinishResolvers = new CopyOnWriteArrayList<>();
    }

    protected final List<AUserContextCreateResolver> userContextCreateResolvers;
    protected final List<AUserContextFinishResolver> userContextFinishResolvers;

    @Override
    public void init() {
        this.userContextCreateResolvers.add(this.factoryManager.create(UserContextCreateContent.class));
        this.userContextCreateResolvers.add(this.factoryManager.create(UserContextCreateProcessAndThread.class));

        this.userContextFinishResolvers.add(this.factoryManager.create(UserContextFinishProcessAndThread.class));

        Collections.sort(this.userContextCreateResolvers);
        Collections.sort(this.userContextFinishResolvers);
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

    public UserContextCreateBuilder createUserContextCreator() {
        AuxiliaryProcessorMediator processorMediator = this.factoryManager.create(AuxiliaryProcessorMediator.class);

        for (AUserContextCreateResolver userContextCreateResolver : this.userContextCreateResolvers) {
            userContextCreateResolver.resolve(processorMediator);
        }

        UserContextCreateBuilder userContextCreateBuilder = this.factoryManager.create(UserContextCreateBuilder.class);

        userContextCreateBuilder.processorMediator = processorMediator;
        userContextCreateBuilder.factory = this;

        return userContextCreateBuilder;
    }

    public UserContextFinishBuilder createUserContextFinish() {
        AuxiliaryProcessorMediator processorMediator = this.factoryManager.create(AuxiliaryProcessorMediator.class);

        for (AUserContextFinishResolver userContextFinishResolver : this.userContextFinishResolvers) {
            userContextFinishResolver.resolve(processorMediator);
        }

        UserContextFinishBuilder userContextFinishBuilder = this.factoryManager.create(UserContextFinishBuilder.class);

        userContextFinishBuilder.processorMediator = processorMediator;
        userContextFinishBuilder.factory = this;

        return userContextFinishBuilder;
    }
}
