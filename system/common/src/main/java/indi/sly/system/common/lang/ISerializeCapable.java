package indi.sly.system.common.lang;

import java.io.*;

public interface ISerializeCapable<T> extends Externalizable, IDeepCloneCapable<T> {
    void readExternal(ObjectInput in) throws IOException, ClassNotFoundException;

    void writeExternal(ObjectOutput out) throws IOException;
}
