package indi.sly.system.kernel.core;

import indi.sly.system.common.values.NoneDefinition;
import indi.sly.system.kernel.core.boot.prototypes.IStartupCapable;
import indi.sly.system.kernel.core.prototypes.ACacheableObject;
import indi.sly.system.kernel.core.prototypes.ADefinitionObject;
import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.kernel.core.values.NoneCacheEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

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
