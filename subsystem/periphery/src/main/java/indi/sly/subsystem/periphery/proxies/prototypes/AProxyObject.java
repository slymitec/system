package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AObject;
import indi.sly.subsystem.periphery.proxies.values.RemoteTypes;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;

public abstract class AProxyObject extends AObject {
    protected ProxyFactory factory;
    private RemoteObject remote;

    public final void setRemote(RemoteObject remote) {
        this.remote = remote;
    }

    public final boolean isAlive() {
        return this.remote.isAlive();
    }

    public final boolean isExpired() {
        return this.remote.isExpired();
    }

    public final void expire(long duration) {
        this.remote.expire(duration);
    }

    @SuppressWarnings("unchecked")
    protected <T> T invoke(String method, Class<T> returnClazz, Object... args) {
        RemoteObject invokeRemote = this.remote.invoke(method, args);

        if (ClassUtil.isThisOrSuperContain(returnClazz, AProxyObject.class)) {
            if (LogicalUtil.allNotEqual(invokeRemote.getType(), RemoteTypes.OBJECT)) {
                throw new StatusRelationshipErrorException();
            }

            Class<? extends AProxyObject> invokeClazz = this.factory.getProxyObjects().getOrDefault(invokeRemote.getRemoteClazz(), null);
            if (ObjectUtil.isAnyNull(invokeClazz) || !returnClazz.equals(invokeClazz)) {
                throw new StatusRelationshipErrorException();
            }

            return (T) this.factory.buildProxy(invokeClazz, invokeRemote);
        } else {
            if (LogicalUtil.allNotEqual(invokeRemote.getType(), RemoteTypes.VALUE)) {
                throw new StatusRelationshipErrorException();
            }

            if (!ClassUtil.getSimpleName(returnClazz).equals(invokeRemote.getRemoteClazz())) {
                throw new StatusRelationshipErrorException();
            }

            if (!returnClazz.equals(Void.class)) {
                return ObjectUtil.transferFromString(returnClazz, invokeRemote.getRemoteValue());
            } else {
                return null;
            }
        }
    }
}
