package indi.sly.system.kernel.core.values;

import indi.sly.system.common.lang.StatusNotSupportedException;

public abstract class ADefinition<T> extends AValue<T> {
    @Override
    public T deepClone() {
        throw new StatusNotSupportedException();
    }
}
