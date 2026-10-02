package indi.sly.subsystem.periphery.proxies.instances.core;

import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import indi.sly.system.common.supports.ObjectUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SystemVersionProxyObject extends AProxyObject {
    public String getSystemVersion() {
        return ObjectUtil.transferFromString(String.class, this.remote.invoke("getSystemVersion", String.class).getValue());
    }
}
