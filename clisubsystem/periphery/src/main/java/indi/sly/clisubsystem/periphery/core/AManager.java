package indi.sly.clisubsystem.periphery.core;

import indi.sly.clisubsystem.periphery.core.boot.prototypes.IStartupCapable;
import indi.sly.clisubsystem.periphery.core.prototypes.AObject;

import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AManager extends AObject implements IStartupCapable {
    @Override
    public void startup(long startup) {
    }

    @Override
    public void shutdown() {
    }

    public void check() {
    }
}
