package indi.sly.subsystem.periphery.proxies.instances.objects;

import indi.sly.subsystem.periphery.proxies.instances.IProxy;
import indi.sly.subsystem.periphery.proxies.instances.security.values.AccessControlRecord;
import indi.sly.subsystem.periphery.proxies.instances.security.values.SecurityDescriptorSummaryRecord;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ISecurityDescriptorProxy extends IProxy {
    List<SecurityDescriptorSummaryRecord> getSummary();

    boolean isInherit();

    void setInherit(boolean inherit);

    Set<UUID> getOwners();

    void setOwners(Set<UUID> owners);

    void setPermissions(Set<AccessControlRecord> permissions);

    void setAudits(Set<AccessControlRecord> audits);
}