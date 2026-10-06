package indi.sly.subsystem.periphery.proxies.instances.processes.prototypes;

import indi.sly.subsystem.periphery.proxies.instances.objects.prototypes.InfoProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.objects.values.InfoOpenRecord;
import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import indi.sly.system.common.values.PathRecord;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessInfoEntryProxyObject extends AProxyObject {
    public UUID getIndex() {
        RemoteObject remote = this.remote.invoke("getIndex");

        return this.factory.getValue(UUID.class, remote);
    }

    public Map<Long, Long> getDate() {
        RemoteObject remote = this.remote.invoke("getDate");

        return this.factory.getMapValue(Long.class, Long.class, remote);
    }

    public PathRecord getPath() {
        RemoteObject remote = this.remote.invoke("getPath");

        return this.factory.getValue(PathRecord.class, remote);
    }

    public InfoOpenRecord getOpen() {
        RemoteObject remote = this.remote.invoke("getOpen");

        return this.factory.getValue(InfoOpenRecord.class, remote);
    }

    public InfoProxyObject getInfo() {
        RemoteObject remote = this.remote.invoke("getInfo");

        return this.factory.getValue(InfoProxyObject.class, remote);
    }
}
