package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.supports.ArrayUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.values.ACacheEntity;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RIndex;

import java.util.*;

@REntity
public class PortCacheEntity extends ACacheEntity {
    public PortCacheEntity() {
        this.sourceProcessIds = new HashSet<>();
        this.value = ArrayUtil.EMPTY_BYTES;
    }

    @RIndex
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
    public boolean equals(Object o) {
        if (!(o instanceof PortCacheEntity cache)) return false;
        return Objects.equals(id, cache.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
