package indi.sly.subsystem.periphery.proxies.instances;

import java.util.UUID;

public interface IProxy {
    UUID getHandle();

    void die();
}
