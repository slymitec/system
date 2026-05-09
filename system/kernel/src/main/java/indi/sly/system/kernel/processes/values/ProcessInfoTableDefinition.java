package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;
import java.util.Map.Entry;

public class ProcessInfoTableDefinition extends ADefinition {
    public ProcessInfoTableDefinition() {
        this.indexTable = new Hashtable<>();
        this.idTable = new HashMap<>();
    }

    private final Map<UUID, ProcessInfoEntryDefinition> indexTable;
    private final Map<UUID, ProcessInfoEntryDefinition> idTable;

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

    public ProcessInfoEntryDefinition getByIndex(UUID index) {
        ProcessInfoEntryDefinition infoEntry = this.indexTable.getOrDefault(index, null);

        if (ObjectUtil.isAnyNull(infoEntry)) {
            throw new StatusNotExistedException();
        }

        return infoEntry;
    }

    public ProcessInfoEntryDefinition getByID(UUID id) {
        ProcessInfoEntryDefinition infoEntry = this.idTable.getOrDefault(id, null);

        if (ObjectUtil.isAnyNull(infoEntry)) {
            throw new StatusNotExistedException();
        }

        return infoEntry;
    }

    public void add(ProcessInfoEntryDefinition infoEntry) {
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
        ProcessInfoEntryDefinition infoEntry = this.indexTable.remove(index);

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
        ProcessInfoTableDefinition that = (ProcessInfoTableDefinition) o;
        return indexTable.equals(that.indexTable) && idTable.equals(that.idTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indexTable, idTable);
    }
}
