package indi.sly.subsystem.periphery.proxies.instances.core;

import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DateTimeProxyObject extends AProxyObject {
//    public long getCurrent() {
//        return this.invoke("getCurrent", Long.class);
//    }
//
//    public void correct(long dateTime) {
//        this.invoke("correct", Void.class, dateTime);
//    }
}
