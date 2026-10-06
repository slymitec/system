package indi.sly.subsystem.periphery.proxies.instances.objects.prototypes;

import indi.sly.subsystem.periphery.proxies.instances.objects.values.InfoOpenRecord;
import indi.sly.subsystem.periphery.proxies.instances.security.values.SecurityDescriptorSummaryRecord;
import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.values.PathRecord;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DumpProxyObject extends AProxyObject {
    public Map<Long, Long> getDate() {
        RemoteObject remote = this.remote.invoke("getDate");

        return this.factory.getMapValue(Long.class, Long.class, remote);
    }

    public UUID getProcessId() {
        RemoteObject remote = this.remote.invoke("getProcessId");

        return this.factory.getValue(UUID.class, remote);
    }

    public UUID getAccountId() {
        RemoteObject remote = this.remote.invoke("getAccountId");

        return this.factory.getValue(UUID.class, remote);
    }

    public PathRecord getPath() {
        RemoteObject remote = this.remote.invoke("getPath");

        return this.factory.getValue(PathRecord.class, remote);
    }

    public InfoOpenRecord getInfoOpen() {
        RemoteObject remote = this.remote.invoke("getInfoOpen");

        return this.factory.getValue(InfoOpenRecord.class, remote);
    }

    public List<SecurityDescriptorSummaryRecord> getSecurityDescriptorSummary() {
        RemoteObject remote = this.remote.invoke("getSecurityDescriptorSummary");

        return this.factory.getListValue(SecurityDescriptorSummaryRecord.class, remote);
    }
}
