package indi.sly.subsystem.periphery.proxies.instances.core;

import indi.sly.subsystem.periphery.proxies.prototypes.AProxyManager;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CoreProxyManager extends AProxyManager {
    public SystemVersionProxyObject getSystemVersion() {
        return this.invoke("getSystemVersion", SystemVersionProxyObject.class);
    }

    public DateTimeProxyObject getDateTime() {
        return this.invoke("getDateTime", DateTimeProxyObject.class);
    }
}
