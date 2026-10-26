package indi.sly.subsystem.periphery.proxies.instances.objects;

import indi.sly.subsystem.periphery.proxies.instances.IProxyManager;
import indi.sly.system.common.values.PathRecord;

public interface IObjectProxyManager extends IProxyManager {
    IInfoProxy get(PathRecord path);
}