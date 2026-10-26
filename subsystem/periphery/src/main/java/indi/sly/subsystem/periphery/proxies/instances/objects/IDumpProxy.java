package indi.sly.subsystem.periphery.proxies.instances.objects;

import indi.sly.subsystem.periphery.proxies.instances.IProxy;
import indi.sly.subsystem.periphery.proxies.instances.objects.values.InfoOpenRecord;
import indi.sly.subsystem.periphery.proxies.instances.security.values.SecurityDescriptorSummaryRecord;
import indi.sly.system.common.values.PathRecord;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IDumpProxy extends IProxy {
    Map<Long, Long> getDate();

    UUID getProcessId();

    UUID getAccountId();

    PathRecord getPath();

    InfoOpenRecord getInfoOpen();

    List<SecurityDescriptorSummaryRecord> getSecurityDescriptorSummary();
}