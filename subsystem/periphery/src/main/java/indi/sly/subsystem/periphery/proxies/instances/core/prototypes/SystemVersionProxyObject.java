package indi.sly.subsystem.periphery.proxies.instances.core.prototypes;

import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SystemVersionProxyObject extends AProxyObject {
    public String getSystemVersion() {
        RemoteObject remote = this.remote.invoke("getSystemVersion");

        return this.factory.getValue(String.class, remote);
    }
}
