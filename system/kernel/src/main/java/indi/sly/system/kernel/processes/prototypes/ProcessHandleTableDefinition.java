package indi.sly.system.kernel.processes.prototypes;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;
import java.util.Map.Entry;

import indi.sly.system.common.exceptions.StatusAlreadyExistedException;
import indi.sly.system.common.exceptions.StatusNotExistedException;
import indi.sly.system.common.support.IDeepCloneable;
import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;

public class ProcessHandleTableDefinition implements ISerializable<ProcessHandleTableDefinition> {
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

        if (ObjectUtils.isAnyNull(handleEntry)) {
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

        if (ObjectUtils.isAnyNull(handleEntry)) {
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
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public ProcessHandleTableDefinition deepClone() {
        ProcessHandleTableDefinition processHandleTable = new ProcessHandleTableDefinition();

        processHandleTable.handleTable.putAll(this.handleTable);

        return processHandleTable;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int valueInteger;

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.handleTable.put(UUIDUtils.readExternal(in), ObjectUtils.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtils.writeExternalInteger(out, this.handleTable.size());
        for (Entry<UUID, ProcessHandleEntryDefinition> pair : this.handleTable.entrySet()) {
            UUIDUtils.writeExternal(out, pair.getKey());
            ObjectUtils.writeExternal(out, pair.getValue());
        }
    }
}
