package indi.sly.subsystem.periphery.core;

import indi.sly.subsystem.periphery.core.boot.prototypes.IStartupCapable;

import indi.sly.subsystem.periphery.core.prototypes.ADefinitionObject;
import indi.sly.system.common.values.NoneDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AManager extends ADefinitionObject<NoneDefinition> implements IStartupCapable {
    @Override
    public void startup(long startup) {
    }

    @Override
    public void shutdown() {
    }

    public void check() {
    }
}
