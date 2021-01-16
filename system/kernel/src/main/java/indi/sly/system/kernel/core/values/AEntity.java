package indi.sly.system.kernel.core.values;

import indi.sly.system.common.lang.ISerializeCapable;

public abstract class AEntity<T> extends AValue<T> implements IIDCapable, ISerializeCapable<T> {
    public AEntity() {
        super();
    }
}
