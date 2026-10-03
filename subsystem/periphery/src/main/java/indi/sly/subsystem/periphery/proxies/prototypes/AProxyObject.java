package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AObject;
import indi.sly.system.common.supports.ObjectUtil;

import java.util.UUID;

public abstract class AProxyObject extends AObject {
    protected ProxyFactory factory;
    protected RemoteObject remote;

    public UUID getHandle() {
        return ObjectUtil.transferFromString(UUID.class, this.remote.getValue());
    }

    public void die() {
        this.remote.invoke("unCache", Void.class);
    }
}
