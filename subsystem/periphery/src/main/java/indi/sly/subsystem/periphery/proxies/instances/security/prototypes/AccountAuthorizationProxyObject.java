package indi.sly.subsystem.periphery.proxies.instances.security.prototypes;

import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountAuthorizationProxyObject extends AProxyObject {
    public Map<Long, Long> getDate() {
        RemoteObject remote = this.remote.invoke("getDate");

        return this.factory.getMapValue(Long.class, Long.class, remote);
    }


    public boolean isLegal() {
        RemoteObject remote = this.remote.invoke("isLegal");

        return this.factory.getValue(Boolean.class, remote);
    }
}