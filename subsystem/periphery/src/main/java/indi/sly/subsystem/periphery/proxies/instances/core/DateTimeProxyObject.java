package indi.sly.subsystem.periphery.proxies.instances.core;

import indi.sly.subsystem.periphery.proxies.prototypes.AProxy;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.time.Clock;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DateTimeProxyObject extends AProxy {
    public long getCurrent() {
        return this.invoke("getCurrent", Long.class);
    }

    public void correct(long dateTime) {
        this.invoke("correct", Void.class, dateTime);
    }
}
