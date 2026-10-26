package indi.sly.subsystem.periphery.proxies.instances.security.instances;

import indi.sly.subsystem.periphery.proxies.instances.objects.IAInfoContentProxy;
import indi.sly.subsystem.periphery.proxies.instances.security.values.UserIdRecord;
import indi.sly.system.common.values.PathRecord;

import java.util.Set;
import java.util.UUID;

public interface IAuditContentProxy extends IAInfoContentProxy {
    UUID getProcessId();

    UUID getAccountId();

    PathRecord getPath();

    Set<UserIdRecord> getUserIds();

    long getAudit();
}
