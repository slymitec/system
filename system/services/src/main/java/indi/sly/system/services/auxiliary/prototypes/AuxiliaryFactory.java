package indi.sly.system.services.auxiliary.prototypes;

import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.services.auxiliary.prototypes.processors.AAuxiliaryResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuxiliaryFactory extends AFactory {
    protected List<AAuxiliaryResolver> auxilityResolvers;

    @Override
    public void init() {
    }

    public UserContextBuilder createUserContext() {
        UserContextBuilder userContextBuilder = this.factoryManager.create(UserContextBuilder.class);

        userContextBuilder.factory = this;

        return userContextBuilder;
    }
}
