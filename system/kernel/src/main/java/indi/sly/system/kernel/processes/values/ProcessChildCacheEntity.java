package indi.sly.system.kernel.processes.values;

import indi.sly.system.kernel.core.values.ACacheEntity;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RObjectField;

import java.util.Objects;

@REntity
public class ProcessChildCacheEntity extends ACacheEntity {
    @RObjectField
    private ProcessCacheEntity process;

    public ProcessCacheEntity getProcess() {
        return process;
    }

    public void setProcess(ProcessCacheEntity process) {
        this.process = process;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProcessChildCacheEntity that = (ProcessChildCacheEntity) o;
        return Objects.equals(process, that.process);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), process);
    }
}
