package indi.sly.subsystem.periphery.proxies.instances.security;

import indi.sly.subsystem.periphery.proxies.instances.IProxy;

import java.util.Set;
import java.util.UUID;

public interface IAccountProxy extends IProxy {
    UUID getId();

    String getName();

    String getPassword();

    void setPassword(String password);

    Set<IGroupProxy> getGroups();

    void setGroups(Set<IGroupProxy> groups);

    IGroupTokenProxy getToken();

    IAccountSessionsProxy getSessions();
}
