package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.kernel.core.values.ACacheEntity;

public class AChildCacheableObject<T extends ACacheEntity, P extends ACacheableObject<?>> extends ACacheableObject<T> {
    @SuppressWarnings("NullAway.Init")
    protected P base;

    public void setBase(P base) {
        this.base = base;
    }
}
