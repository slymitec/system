package indi.sly.system.kernel.processes.values;

import com.redis.om.spring.annotations.Document;
import indi.sly.system.kernel.core.values.ACacheEntity;
import org.springframework.data.annotation.Reference;

import java.util.Objects;
import java.util.UUID;

@Document("ProcessInfoEntryObject")
public class ProcessInfoEntryCacheEntity extends ACacheEntity {
    @Reference
    private ProcessChildCacheEntity processInfoTable;

    private UUID index;

    public ProcessChildCacheEntity getProcessInfoTable() {
        return processInfoTable;
    }

    public void setProcessInfoTable(ProcessChildCacheEntity processInfoTable) {
        this.processInfoTable = processInfoTable;
    }

    public UUID getIndex() {
        return index;
    }

    public void setIndex(UUID index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProcessInfoEntryCacheEntity that = (ProcessInfoEntryCacheEntity) o;
        return Objects.equals(processInfoTable, that.processInfoTable) && Objects.equals(index, that.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), processInfoTable, index);
    }
}
