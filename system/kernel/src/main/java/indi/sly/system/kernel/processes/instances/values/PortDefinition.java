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

public class PortDefinition extends ADefinition<PortDefinition> {
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

    @Override
    public PortDefinition deepClone() {
        PortDefinition signal = new PortDefinition();

        signal.processID = this.processID;
        signal.sourceProcessIDs.addAll(this.sourceProcessIDs);
        signal.value = ArrayUtil.copyBytes(this.value);
        signal.limit = this.limit;

        return signal;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.processID = UUIDUtil.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.sourceProcessIDs.add(UUIDUtil.readExternal(in));
        }

        this.value = NumberUtil.readExternalBytes(in);
        this.limit = NumberUtil.readExternalInteger(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtil.writeExternal(out, this.processID);

        NumberUtil.writeExternalInteger(out, this.sourceProcessIDs.size());
        for (UUID pair : this.sourceProcessIDs) {
            UUIDUtil.writeExternal(out, pair);
        }

        NumberUtil.writeExternalBytes(out, this.value);
        NumberUtil.writeExternalLong(out, this.limit);
    }
}
