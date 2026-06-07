package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AFactory;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProxyFactory extends AFactory {


    public void buildProxy(Class<? extends AProxy> clazz) {

    }
}
