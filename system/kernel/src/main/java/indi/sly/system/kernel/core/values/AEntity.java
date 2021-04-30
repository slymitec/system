package indi.sly.system.kernel.core.values;

import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.values.AValue;
import indi.sly.system.common.values.IIDCapable;

public abstract class AEntity<T> extends AValue<T> implements IIDCapable, ISerializeCapable<T> {
    public AEntity() {
        super();
    }
}
