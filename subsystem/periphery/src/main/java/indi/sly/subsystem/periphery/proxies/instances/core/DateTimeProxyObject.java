package indi.sly.subsystem.periphery.proxies.instances.core;

import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.system.common.supports.ObjectUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DateTimeProxyObject extends AProxyObject {
    public long getCurrent() {
        return ObjectUtil.transferFromString(Long.class, this.remote.invoke("getCurrent", String.class).getValue());
    }

    public void correct(long dateTime) {
        this.remote.invoke("correct", String.class, dateTime);
    }
}
