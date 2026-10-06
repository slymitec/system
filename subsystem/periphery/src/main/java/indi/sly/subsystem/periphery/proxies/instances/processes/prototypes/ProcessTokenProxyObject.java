package indi.sly.subsystem.periphery.proxies.instances.processes.prototypes;

import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessTokenProxyObject extends AProxyObject {
    public UUID getAccountId() {
        RemoteObject remote = this.remote.invoke("getAccountId");

        return this.factory.getValue(UUID.class, remote);
    }

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

    public Set<UUID> getRoles() {
        RemoteObject remote = this.remote.invoke("getRoles");

        return this.factory.getSetValue(UUID.class, remote);
    }

    public void initDefaultRoles() {
        this.remote.invoke("initDefaultRoles");
    }

    public void addRoles(Set<UUID> roles) {
        this.remote.invoke("addRoles", roles);
    }
}
