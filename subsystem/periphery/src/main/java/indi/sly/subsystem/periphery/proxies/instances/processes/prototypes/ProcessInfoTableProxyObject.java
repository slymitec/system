package indi.sly.subsystem.periphery.proxies.instances.processes.prototypes;

import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessInfoTableProxyObject extends AProxyObject {
    public Set<UUID> list() {
        RemoteObject remote = this.remote.invoke("list");

        return this.factory.getSetValue(UUID.class, remote);
    }

    public boolean containByIndex(UUID index) {
        RemoteObject remote = this.remote.invoke("containByIndex", index);

        return this.factory.getValue(Boolean.class, remote);
    }

    public boolean containById(UUID id) {
        RemoteObject remote = this.remote.invoke("containById", id);

        return this.factory.getValue(Boolean.class, remote);
    }

    public ProcessInfoEntryProxyObject getByIndex(UUID index) {
        RemoteObject remote = this.remote.invoke("getByIndex", index);

        return this.factory.buildProxy(ProcessInfoEntryProxyObject.class, remote);
    }

    public ProcessInfoEntryProxyObject getById(UUID id) {
        RemoteObject remote = this.remote.invoke("getById", id);

        return this.factory.buildProxy(ProcessInfoEntryProxyObject.class, remote);
    }
}