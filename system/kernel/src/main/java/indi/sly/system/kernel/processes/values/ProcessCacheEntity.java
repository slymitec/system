package indi.sly.system.kernel.processes.values;

import indi.sly.system.kernel.core.values.ACacheEntity;
import org.redisson.api.annotation.REntity;

import java.util.Objects;
import java.util.UUID;

@REntity
public class ProcessCacheEntity extends ACacheEntity {
    private UUID processId;

    public UUID getProcessId() {
        return this.processId;
    }

    public void setProcessId(UUID processId) {
        this.processId = processId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProcessCacheEntity that = (ProcessCacheEntity) o;
        return Objects.equals(processId, that.processId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), processId);
    }
}
