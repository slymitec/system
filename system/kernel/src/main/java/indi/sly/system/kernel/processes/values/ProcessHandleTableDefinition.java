package indi.sly.system.kernel.processes.values;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;
import java.util.Map.Entry;

import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

public class ProcessHandleTableDefinition extends ADefinition<ProcessHandleTableDefinition> {
    public ProcessHandleTableDefinition() {
        this.handleTable = new Hashtable<>();
    }

    private final Map<UUID, ProcessHandleEntryDefinition> handleTable;

    public int size() {
        return this.handleTable.size();
    }

    public boolean isEmpty() {
        return this.handleTable.isEmpty();
    }

    public boolean contain(UUID handle) {
        return this.handleTable.containsKey(handle);
    }

    public ProcessHandleEntryDefinition get(UUID handle) {
        ProcessHandleEntryDefinition handleEntry = this.handleTable.getOrDefault(handle, null);

        if (ObjectUtil.isAnyNull(handleEntry)) {
            throw new StatusNotExistedException();
        }

        return handleEntry;
    }

    public void add(UUID handle, ProcessHandleEntryDefinition handleEntry) {
        if (this.handleTable.containsKey(handle)) {
            throw new StatusAlreadyExistedException();
        }

        this.handleTable.put(handle, handleEntry);
    }

    public void delete(UUID handle) {
        ProcessHandleEntryDefinition handleEntry = this.handleTable.remove(handle);

        if (ObjectUtil.isAnyNull(handleEntry)) {
            throw new StatusNotExistedException();
        }
    }

    public Set<UUID> list() {
        return Collections.unmodifiableSet(this.handleTable.keySet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessHandleTableDefinition that = (ProcessHandleTableDefinition) o;
        return handleTable.equals(that.handleTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(handleTable);
    }

    @Override
    public ProcessHandleTableDefinition deepClone() {
        ProcessHandleTableDefinition definition = new ProcessHandleTableDefinition();

        definition.handleTable.putAll(this.handleTable);

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.handleTable.put(UUIDUtil.readExternal(in), ObjectUtil.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtil.writeExternalInteger(out, this.handleTable.size());
        for (Entry<UUID, ProcessHandleEntryDefinition> pair : this.handleTable.entrySet()) {
            UUIDUtil.writeExternal(out, pair.getKey());
            ObjectUtil.writeExternal(out, pair.getValue());
        }
    }
}
