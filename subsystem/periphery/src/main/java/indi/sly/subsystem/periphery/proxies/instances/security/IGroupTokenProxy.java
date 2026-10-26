package indi.sly.subsystem.periphery.proxies.instances.security;

import indi.sly.subsystem.periphery.proxies.instances.IProxy;

import java.util.Map;

public interface IGroupTokenProxy extends IProxy {
    long getPrivileges();

    void setPrivileges(long privileges);

    Map<Long, Integer> getLimits();

    void setLimits(Map<Long, Integer> limits);
}
