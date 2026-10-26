package indi.sly.subsystem.periphery.proxies.instances.security;

import indi.sly.subsystem.periphery.proxies.instances.IProxy;

import java.util.UUID;

public interface IGroupProxy extends IProxy {
    UUID getId();

    String getName();

    IGroupTokenProxy getToken();
}
