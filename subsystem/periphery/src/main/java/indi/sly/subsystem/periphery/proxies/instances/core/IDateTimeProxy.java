package indi.sly.subsystem.periphery.proxies.instances.core;

import indi.sly.subsystem.periphery.proxies.instances.IProxy;

public interface IDateTimeProxy extends IProxy {
    long getCurrent();

    void correct(long dateTime);
}
