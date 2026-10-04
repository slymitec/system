package indi.sly.subsystem.periphery.proxies.instances.core.prototypes;

import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DateTimeProxyObject extends AProxyObject {
    public long getCurrent() {
        RemoteObject remote = this.remote.invoke("getCurrent", Long.class);

        return this.factory.getValue(Long.class, remote);
    }

    public void correct(long dateTime) {
        this.remote.invoke("correct", Void.class, dateTime);
    }
}
