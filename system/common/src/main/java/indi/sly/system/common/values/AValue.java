package indi.sly.system.common.values;

import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.AObject;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class AValue<T> extends AObject implements ISerializeCapable<T> {
    public AValue() {
        super();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Object value;

        try {
            value = this.deepClone();
        } catch (StatusNotSupportedException e) {
            value = super.clone();
        }

        return value;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
    }
}
