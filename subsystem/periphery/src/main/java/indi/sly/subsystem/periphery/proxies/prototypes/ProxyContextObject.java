package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.ACacheableObject;
import indi.sly.subsystem.periphery.proxies.values.ProxyContextCacheEntity;

public class ProxyContextObject extends ACacheableObject<ProxyContextCacheEntity> {
    public void getHandleTable() {
    }

    public <T extends AProxyManager> T getManager(Class<T> clazz) {
        return null;
    }
}
