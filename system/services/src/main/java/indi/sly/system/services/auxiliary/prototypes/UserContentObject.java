package indi.sly.system.services.auxiliary.prototypes;

import indi.sly.system.kernel.core.prototypes.AIndependentValueProcessObject;
import indi.sly.system.services.auxiliary.values.UserContentDefinition;
import indi.sly.system.services.auxiliary.values.UserContentResponseRawDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContentObject extends AIndependentValueProcessObject<UserContentDefinition> {
    public String getJob() {
        this.init();

        return this.value.getJob();
    }

    public String getMethod() {
        this.init();

        return this.value.getMethod();
    }

    public void run() {

    }

    public UserContentResponseRawDefinition getResponse() {
        return null;
    }
}
