package indi.sly.subsystem.periphery.core.environment.containers;

import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserSpace extends ASystemSpace {
    public UserSpace() {
    }
}
