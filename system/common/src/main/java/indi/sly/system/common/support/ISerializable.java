package indi.sly.system.common.support;

import java.io.*;

public interface ISerializable<T> extends Externalizable, IDeepCloneable<T> {
    void readExternal(ObjectInput in) throws IOException, ClassNotFoundException;

    void writeExternal(ObjectOutput out) throws IOException;
}
