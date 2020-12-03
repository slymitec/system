package indi.sly.system.kernel.sessions;

import indi.sly.system.kernel.core.AManager;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionManager extends AManager {
    @Override
    public void startup(long startupTypes) {
    }

    @Override
    public void shutdown() {
    }


}
