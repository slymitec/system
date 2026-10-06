package indi.sly.subsystem.periphery.proxies.instances.security.prototypes;

import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.rmi.Remote;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountSessionsProxyObject extends AProxyObject {
    public Set<UUID> listSessions() {
        RemoteObject remote = this.remote.invoke("listSessions");

        return this.factory.getSetValue(UUID.class, remote);
    }

    public void addSession(UUID sessionId) {
        RemoteObject remote = this.remote.invoke("addSession", sessionId);
    }

    public void deleteSession(UUID sessionId) {
        this.remote.invoke("deleteSession", sessionId);
    }
}
