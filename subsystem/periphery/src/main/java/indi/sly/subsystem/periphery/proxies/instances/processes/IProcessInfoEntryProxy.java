package indi.sly.subsystem.periphery.proxies.instances.processes;

import indi.sly.subsystem.periphery.proxies.instances.IProxy;
import indi.sly.subsystem.periphery.proxies.instances.objects.IInfoProxy;
import indi.sly.subsystem.periphery.proxies.instances.objects.values.InfoOpenRecord;
import indi.sly.system.common.values.PathRecord;

import java.util.Map;
import java.util.UUID;

public interface IProcessInfoEntryProxy extends IProxy {
    UUID getIndex();

    Map<Long, Long> getDate();

    PathRecord getPath();

    InfoOpenRecord getOpen();

    IInfoProxy getInfo();
}