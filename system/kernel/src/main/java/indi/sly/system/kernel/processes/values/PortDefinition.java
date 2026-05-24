package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.supports.ArrayUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.ADefinition;

import java.util.*;

public class PortDefinition extends ADefinition {
    public PortDefinition() {
        this.sourceProcessIds = new HashSet<>();
        this.value = ArrayUtil.EMPTY_BYTES;
    }

    private UUID processId;
    private final Set<UUID> sourceProcessIds;
    private byte[] value;
    private int limit;

    public UUID getProcessId() {
        return this.processId;
    }

    public void setProcessId(UUID processId) {
        this.processId = processId;
    }

    public Set<UUID> getSourceProcessIds() {
        return this.sourceProcessIds;
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
    public final boolean equals(Object o) {
        if (!(o instanceof PortDefinition that)) return false;
        return limit == that.limit && Objects.equals(processId, that.processId) && Objects.equals(sourceProcessIds, that.sourceProcessIds) && Objects.deepEquals(value, that.value);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(processId, sourceProcessIds, Arrays.hashCode(value), limit);
    }
}
