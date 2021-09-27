package indi.sly.system.services.auxiliary.prototypes;

import indi.sly.system.kernel.core.prototypes.ABuilder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextBuilder extends ABuilder {
    protected AuxiliaryFactory factory;

    public UserContextObject create(String userRequest) {
        return null;
    }
}
