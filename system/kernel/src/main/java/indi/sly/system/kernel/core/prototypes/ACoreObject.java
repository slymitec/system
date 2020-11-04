package indi.sly.system.kernel.core.prototypes;

import javax.inject.Named;

import indi.sly.system.kernel.core.prototypes.FactoryManager;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ACoreObject {
    protected FactoryManager factoryManager;
}
