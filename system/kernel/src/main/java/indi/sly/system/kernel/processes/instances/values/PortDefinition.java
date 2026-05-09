package indi.sly.system.kernel.processes.instances.values;

import indi.sly.system.common.supports.ArrayUtil;
import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class PortDefinition extends ADefinition {
    public PortDefinition() {
        this.sourceProcessIDs = new HashSet<>();
        this.value = ArrayUtil.EMPTY_BYTES;
    }

    private UUID processID;
    private final Set<UUID> sourceProcessIDs;
    private byte[] value;
    private int limit;

    public UUID getProcessID() {
        return this.processID;
    }

    public void setProcessID(UUID processID) {
        this.processID = processID;
    }

    public Set<UUID> getSourceProcessIDs() {
        return this.sourceProcessIDs;
    }

    public int size() {
        return this.value.length;
    }

    public byte[] getValue() {
        return this.value;
    }

    public void setValue(byte[] value) {
        if (ObjectUtil.isAnyNull(value)) {
            this.value = ArrayUtil.EMPTY_BYTES;
        } else {
            this.value = value;
        }
    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortDefinition that = (PortDefinition) o;
        return limit == that.limit &&
                Objects.equals(processID, that.processID) &&
                sourceProcessIDs.equals(that.sourceProcessIDs) &&
                Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(processID, sourceProcessIDs, limit);
        result = 31 * result + Arrays.hashCode(value);
        return result;
    }
}
