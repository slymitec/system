package indi.sly.subsystem.periphery.proxies.instances.core;

import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.AServiceProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CoreProxyManager extends AServiceProxyObject {
    public SystemVersionProxyObject getSystemVersion() {
        RemoteObject remote = this.remote.invoke("getSystemVersion", SystemVersionProxyObject.class);

        return this.factory.buildProxy(SystemVersionProxyObject.class, remote);
    }

    public DateTimeProxyObject getDateTime() {
        RemoteObject remote = this.remote.invoke("getDateTime", DateTimeProxyObject.class);

        return this.factory.buildProxy(DateTimeProxyObject.class, remote);
    }
}
