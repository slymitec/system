package indi.sly.system.common.lang;

import indi.sly.system.common.lang.IDeepCloneCapable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public interface ISerializeCapable<T> extends Externalizable, IDeepCloneCapable<T> {
    void readExternal(ObjectInput in) throws IOException, ClassNotFoundException;

    void writeExternal(ObjectOutput out) throws IOException;
}
