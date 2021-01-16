package indi.sly.system.kernel.core.values;

import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.AObject;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

public abstract class AValue<T> extends AObject implements ISerializeCapable<T> {
    public AValue() {
        super();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
    }
}
