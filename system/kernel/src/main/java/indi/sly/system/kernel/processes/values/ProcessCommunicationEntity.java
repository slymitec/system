package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.supports.*;
import indi.sly.system.kernel.core.values.APersistentEntity;

import java.util.*;

public class ProcessCommunicationEntity extends APersistentEntity {
    public ProcessCommunicationEntity() {
        this.shared = ArrayUtil.EMPTY_BYTES;
    }

    private byte[] shared;

    public byte[] getShared() {
        return this.shared;
    }

    public void setShared(byte[] shared) {
        this.shared = shared;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProcessCommunicationEntity that = (ProcessCommunicationEntity) o;
        return Objects.deepEquals(shared, that.shared);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(shared);
    }
}
