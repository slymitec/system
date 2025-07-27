package indi.sly.system.common.values;

import indi.sly.system.common.ABase;
import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.lang.StatusDisabilityException;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class AValue<T> extends ABase implements ISerializeCapable<T> {
    public AValue() {
        super();
    }

    @Override
    public Object clone() {
        Object value;

        try {
            value = this.deepClone();
        } catch (StatusDisabilityException e) {
            try {
                value = super.clone();
            } catch (Exception ignored) {
                throw e;
            }
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
