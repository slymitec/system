package indi.sly.subsystem.periphery.proxies.instances.core;

import indi.sly.subsystem.periphery.proxies.instances.IProxyManager;

public interface ICoreProxyManager extends IProxyManager {
    ISystemVersionProxy getSystemVersion();

    IDateTimeProxy getDateTime();
}
