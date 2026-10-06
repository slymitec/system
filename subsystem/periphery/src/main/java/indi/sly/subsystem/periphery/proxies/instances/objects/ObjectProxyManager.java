package indi.sly.subsystem.periphery.proxies.instances.objects;

import indi.sly.subsystem.periphery.proxies.instances.objects.prototypes.InfoProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.AManagerProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import indi.sly.system.common.values.PathRecord;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ObjectProxyManager extends AManagerProxyObject {
    public InfoProxyObject get(PathRecord path) {
        RemoteObject remote = this.remote.invoke("get", path);

        return this.factory.buildProxy(InfoProxyObject.class, remote);
    }
}
