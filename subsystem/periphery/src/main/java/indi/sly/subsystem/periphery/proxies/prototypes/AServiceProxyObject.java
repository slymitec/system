package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.system.common.lang.StatusNotSupportedException;

import java.util.UUID;

public class AServiceProxyObject extends AProxyObject {
    @Override
    public UUID getHandle() {
        throw new StatusNotSupportedException();
    }

    @Override
    public void die() {
        throw new StatusNotSupportedException();
    }
}
