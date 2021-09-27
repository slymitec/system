package indi.sly.system.services.auxiliary.prototypes;

import indi.sly.system.kernel.core.prototypes.AIndependentValueProcessObject;
import indi.sly.system.services.auxiliary.values.UserContextDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextObject extends AIndependentValueProcessObject<UserContextDefinition> {
    public UserContentObject getContent() {
        UserContentObject userContent = this.factoryManager.create(UserContentObject.class);

        return userContent;
    }
}
