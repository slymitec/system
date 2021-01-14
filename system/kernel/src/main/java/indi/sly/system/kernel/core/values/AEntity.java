package indi.sly.system.kernel.core.values;

import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.supports.ObjectUtil;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

public abstract class AEntity<T> implements IIDCapable, ISerializeCapable<T> {
    public AEntity() {
        super();
    }

    protected UUID id;

    @Override
    public UUID getID() {
        return this.id;
    }

    public void setID(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public T deepClone() {
        return null;
    }

    @Override
    public String toString() {
        return ObjectUtil.allNotNull(this.id) ? this.id.toString() : null;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }
}
