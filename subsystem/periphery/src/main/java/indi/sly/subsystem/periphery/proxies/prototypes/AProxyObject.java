package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AObject;
import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;

import java.util.UUID;

public abstract class AProxyObject extends AObject {
    @SuppressWarnings("NullAway.Init")
    protected ProxyFactory factory;
    @SuppressWarnings("NullAway.Init")
    protected RemoteObject remote;

    public UUID getHandle() {
        UUID handle = ObjectUtil.transferFromString(UUID.class, this.remote.getValue());

        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new StatusUnexpectedException();
        }

        return handle;
    }

    public void die() {
        this.remote.invoke("unCache", Void.class);
    }
}
