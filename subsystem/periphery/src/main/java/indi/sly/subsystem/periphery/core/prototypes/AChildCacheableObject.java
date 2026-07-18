package indi.sly.subsystem.periphery.core.prototypes;

import indi.sly.subsystem.periphery.core.values.ACacheEntity;

public class AChildCacheableObject<T extends ACacheEntity, P extends ACacheableObject<?>> extends ACacheableObject<T> {
    protected P base;

    public void setBase(P base) {
        this.base = base;
    }
}
