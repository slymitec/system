package indi.sly.system.common.values;

import indi.sly.system.common.lang.StatusDisabilityException;

public abstract class ADefinition<T> extends AValue<T> {
    @Override
    public T deepClone() {
        throw new StatusDisabilityException();
    }
}
