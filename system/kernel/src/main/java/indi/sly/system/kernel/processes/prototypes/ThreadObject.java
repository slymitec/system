package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.kernel.core.enviroment.UserSpace;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadObject {
    public UserSpace getUserSpace() {
        return null;
    }

}
