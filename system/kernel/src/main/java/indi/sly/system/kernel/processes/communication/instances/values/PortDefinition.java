package indi.sly.system.kernel.processes.communication.instances.values;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.ArrayUtils;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class PortDefinition implements ISerializable<PortDefinition> {
    public PortDefinition() {
        this.sourceProcessIDs = new HashSet<>();
        this.value = ArrayUtils.EMPTY_BYTES;
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
        if (ObjectUtils.isAnyNull(value)) {
            this.value = ArrayUtils.EMPTY_BYTES;
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
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public PortDefinition deepClone() {
        PortDefinition signal = new PortDefinition();

        signal.processID = this.processID;
        signal.sourceProcessIDs.addAll(this.sourceProcessIDs);
        signal.value = ArrayUtils.copyBytes(this.value);
        signal.limit = this.limit;

        return signal;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.processID = UUIDUtils.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.sourceProcessIDs.add(UUIDUtils.readExternal(in));
        }

        this.value = NumberUtils.readExternalBytes(in);
        this.limit = NumberUtils.readExternalInteger(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtils.writeExternal(out, this.processID);

        NumberUtils.writeExternalInteger(out, this.sourceProcessIDs.size());
        for (UUID pair : this.sourceProcessIDs) {
            UUIDUtils.writeExternal(out, pair);
        }

        NumberUtils.writeExternalBytes(out, this.value);
        NumberUtils.writeExternalLong(out, this.limit);
    }
}
