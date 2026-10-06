package indi.sly.subsystem.periphery.proxies.instances.security.prototypes;

import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GroupProxyObject extends AProxyObject {
    public UUID getId() {
        RemoteObject remote = this.remote.invoke("getId");

        return this.factory.getValue(UUID.class, remote);
    }

    public String getName() {
        RemoteObject remote = this.remote.invoke("getName");

        return this.factory.getValue(String.class, remote);
    }

    public GroupTokenProxyObject getToken() {
        RemoteObject remote = this.remote.invoke("getToken");

        return this.factory.buildProxy(GroupTokenProxyObject.class, remote);
    }
}
