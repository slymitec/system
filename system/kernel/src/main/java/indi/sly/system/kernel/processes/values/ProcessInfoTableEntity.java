package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.values.APersistentEntity;

import java.util.*;

public class ProcessInfoTableEntity extends APersistentEntity {
    public ProcessInfoTableEntity() {
        this.indexTable = new Hashtable<>();
        this.idTable = new HashMap<>();
    }

    private final Map<UUID, ProcessInfoEntryEntity> indexTable;
    private final Map<UUID, ProcessInfoEntryEntity> idTable;

    public int size() {
        return this.indexTable.size();
    }

    public boolean isEmpty() {
        return this.indexTable.isEmpty() && this.idTable.isEmpty();
    }

    public boolean containByIndex(UUID index) {
        return this.indexTable.containsKey(index);
    }

    public boolean containByID(UUID id) {
        return this.idTable.containsKey(id);
    }

    public ProcessInfoEntryEntity getByIndex(UUID index) {
        ProcessInfoEntryEntity infoEntry = this.indexTable.getOrDefault(index, null);

        if (ObjectUtil.isAnyNull(infoEntry)) {
            throw new StatusNotExistedException();
        }

        return infoEntry;
    }

    public ProcessInfoEntryEntity getByID(UUID id) {
        ProcessInfoEntryEntity infoEntry = this.idTable.getOrDefault(id, null);

        if (ObjectUtil.isAnyNull(infoEntry)) {
            throw new StatusNotExistedException();
        }

        return infoEntry;
    }

    public void add(ProcessInfoEntryEntity infoEntry) {
        if (this.indexTable.containsKey(infoEntry.getIndex())) {
            throw new StatusAlreadyExistedException();
        }
        if (this.idTable.containsKey(infoEntry.getId())) {
            throw new StatusAlreadyExistedException();
        }

        this.indexTable.put(infoEntry.getIndex(), infoEntry);
        this.idTable.put(infoEntry.getId(), infoEntry);
    }

    public void delete(UUID index) {
        ProcessInfoEntryEntity infoEntry = this.indexTable.remove(index);

        if (ObjectUtil.isAnyNull(infoEntry)) {
            throw new StatusNotExistedException();
        }

        infoEntry = this.idTable.remove(infoEntry.getId());

        if (ObjectUtil.isAnyNull(infoEntry)) {
            throw new StatusNotExistedException();
        }
    }

    public Set<UUID> list() {
        return CollectionUtil.unmodifiable(this.indexTable.keySet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessInfoTableEntity that = (ProcessInfoTableEntity) o;
        return indexTable.equals(that.indexTable) && idTable.equals(that.idTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indexTable, idTable);
    }
}
