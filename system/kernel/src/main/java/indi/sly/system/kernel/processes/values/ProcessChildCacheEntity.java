package indi.sly.system.kernel.processes.values;

import com.redis.om.spring.annotations.Document;
import indi.sly.system.kernel.core.values.ACacheEntity;
import org.springframework.data.annotation.Reference;

import java.util.Objects;

@Document("ProcessChildObject")
public class ProcessChildCacheEntity extends ACacheEntity {
    @Reference
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
