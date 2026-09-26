package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AObject;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;

public abstract class AProxyObject extends AObject {
    protected ProxyFactory factory;
    private RemoteObject remote;

    public final void setRemote(RemoteObject remote) {
        this.remote = remote;
    }

    @SuppressWarnings("unchecked")
    protected <T> T invoke(String method, Class<T> returnClazz, Object... args) {
        RemoteObject invokeRemote = this.remote.invoke(method, args);

        if (ClassUtil.isThisOrSuperContain(returnClazz, AProxyObject.class)) {
            return (T) this.factory.buildProxy((Class<? extends AProxyObject>) returnClazz, invokeRemote);
        } else {
            if (!returnClazz.equals(Void.class)) {
                return ObjectUtil.transferFromString(returnClazz, invokeRemote.getValue());
            } else {
                return null;
            }
        }
    }
}
