package indi.sly.system.common.support;

import java.io.*;

public interface ISerializable extends Externalizable {
    void readExternal(ObjectInput in) throws IOException, ClassNotFoundException;

    void writeExternal(ObjectOutput out) throws IOException;
}
