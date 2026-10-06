package indi.sly.subsystem.periphery.proxies.instances.security.prototypes.instances;

import indi.sly.subsystem.periphery.proxies.instances.objects.prototypes.AInfoContentProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.security.values.UserIdRecord;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import indi.sly.system.common.values.PathRecord;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuditContentProxyObject extends AInfoContentProxyObject {
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

    public Set<UserIdRecord> getUserIds() {
        RemoteObject remote = this.remote.invoke("getUserIds");

        return this.factory.getSetValue(UserIdRecord.class, remote);
    }

    public long getAudit() {
        RemoteObject remote = this.remote.invoke("getAudit");

        return this.factory.getValue(Long.class, remote);
    }
}
