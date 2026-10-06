package indi.sly.subsystem.periphery.proxies.instances.processes.prototypes;

import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessSessionProxyObject extends AProxyObject {
    public UUID getId() {
        RemoteObject remote = this.remote.invoke("getId");

        return this.factory.getValue(UUID.class, remote);
    }

    public void setId(UUID id) {
        this.remote.invoke("setId", id);
    }

    public long getType() {
        RemoteObject remote = this.remote.invoke("getType");

        return this.factory.getValue(long.class, remote);
    }

    public void setType(long type) {
        this.remote.invoke("setType", type);
    }
}
