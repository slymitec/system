package indi.sly.subsystem.periphery.proxies.instances.objects.prototypes;

import indi.sly.subsystem.periphery.proxies.instances.security.values.AccessControlRecord;
import indi.sly.subsystem.periphery.proxies.instances.security.values.SecurityDescriptorSummaryRecord;
import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SecurityDescriptorProxyObject extends AProxyObject {
    public List<SecurityDescriptorSummaryRecord> getSummary() {
        RemoteObject remote = this.remote.invoke("getSummary", List.class);

        return this.factory.getListValue(SecurityDescriptorSummaryRecord.class, remote);
    }

    public boolean isInherit() {
        RemoteObject remote = this.remote.invoke("isInherit", Boolean.class);

        return this.factory.getValue(Boolean.class, remote);
    }

    public void setInherit(boolean inherit) {
        RemoteObject remote = this.remote.invoke("setInherit", Boolean.class, inherit);
    }

    public Set<UUID> getOwners() {
        RemoteObject remote = this.remote.invoke("getOwners", Set.class);

        return this.factory.getSetValue(UUID.class, remote);
    }

    public void setOwners(Set<UUID> owners) {
        this.remote.invoke("setOwners", Void.class, owners);
    }

    public void setPermissions(Set<AccessControlRecord> permissions) {
        this.remote.invoke("setPermissions", Void.class, permissions);
    }

    public void setAudits(Set<AccessControlRecord> audits) {
        this.remote.invoke("setAudits", Void.class, audits);
    }
}
