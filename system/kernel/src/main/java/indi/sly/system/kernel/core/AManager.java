package indi.sly.system.kernel.core;

import indi.sly.system.kernel.core.boot.prototypes.IStartupCapable;
import indi.sly.system.kernel.core.prototypes.APrototype;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AManager extends APrototype implements IStartupCapable {
    @Override
    public void startup(long startupTypes) {
    }

    @Override
    public void shutdown() {
    }

    public void check() {
    }
}
