package indi.sly.subsystem.periphery.proxies.instances.security.prototypes;

import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GroupTokenProxyObject extends AProxyObject {
    public long getPrivileges() {
        RemoteObject remote = this.remote.invoke("getPrivileges");

        return this.factory.getValue(Long.class, remote);
    }

    public void setPrivileges(long privileges) {
        this.remote.invoke("setPrivileges", privileges);
    }

    public Map<Long, Integer> getLimits() {
        RemoteObject remote = this.remote.invoke("getLimits");

        return this.factory.getMapValue(Long.class, Integer.class, remote);
    }

    public void setLimits(Map<Long, Integer> limits) {
        this.remote.invoke("setLimits", limits);
    }
}
