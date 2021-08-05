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

public class ProcessHandleTableDefinition extends ADefinition<ProcessHandleTableDefinition> {
    public ProcessHandleTableDefinition() {
        this.handleTable = new Hashtable<>();
        this.infoTable = new HashMap<>();
    }

    private final Map<UUID, ProcessHandleEntryDefinition> handleTable;
    private final Map<UUID, ProcessHandleEntryDefinition> infoTable;

    public int size() {
        return this.handleTable.size();
    }

    public boolean isEmpty() {
        return this.handleTable.isEmpty() && this.infoTable.isEmpty();
    }

    public boolean containByHandle(UUID handle) {
        return this.handleTable.containsKey(handle);
    }

    public boolean containByInfoID(UUID infoID) {
        return this.infoTable.containsKey(infoID);
    }

    public ProcessHandleEntryDefinition getByHandle(UUID handle) {
        ProcessHandleEntryDefinition handleEntry = this.handleTable.getOrDefault(handle, null);

        if (ObjectUtil.isAnyNull(handleEntry)) {
            throw new StatusNotExistedException();
        }

        return handleEntry;
    }

    public ProcessHandleEntryDefinition getByInfoID(UUID infoID) {
        ProcessHandleEntryDefinition handleEntry = this.infoTable.getOrDefault(infoID, null);

        if (ObjectUtil.isAnyNull(handleEntry)) {
            throw new StatusNotExistedException();
        }

        return handleEntry;
    }

    public void add(ProcessHandleEntryDefinition handleEntry) {
        if (this.handleTable.containsKey(handleEntry.getHandle())) {
            throw new StatusAlreadyExistedException();
        }
        if (this.infoTable.containsKey(handleEntry.getInfoID())) {
            throw new StatusAlreadyExistedException();
        }

        this.handleTable.put(handleEntry.getHandle(), handleEntry);
        this.infoTable.put(handleEntry.getInfoID(), handleEntry);
    }

    public void delete(UUID handle) {
        ProcessHandleEntryDefinition handleEntry = this.handleTable.remove(handle);

        if (ObjectUtil.isAnyNull(handleEntry)) {
            throw new StatusNotExistedException();
        }

        handleEntry = this.infoTable.remove(handleEntry.getInfoID());

        if (ObjectUtil.isAnyNull(handleEntry)) {
            throw new StatusNotExistedException();
        }
    }

    public Set<UUID> list() {
        return CollectionUtil.unmodifiable(this.handleTable.keySet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessHandleTableDefinition that = (ProcessHandleTableDefinition) o;
        return handleTable.equals(that.handleTable) && infoTable.equals(that.infoTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(handleTable, infoTable);
    }

    @Override
    public ProcessHandleTableDefinition deepClone() {
        ProcessHandleTableDefinition definition = new ProcessHandleTableDefinition();

        definition.handleTable.putAll(this.handleTable);
        definition.infoTable.putAll(this.infoTable);

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.handleTable.put(UUIDUtil.readExternal(in), ObjectUtil.readExternal(in));
        }

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.infoTable.put(UUIDUtil.readExternal(in), ObjectUtil.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        NumberUtil.writeExternalInteger(out, this.handleTable.size());
        for (Entry<UUID, ProcessHandleEntryDefinition> pair : this.handleTable.entrySet()) {
            UUIDUtil.writeExternal(out, pair.getKey());
            ObjectUtil.writeExternal(out, pair.getValue());
        }

        NumberUtil.writeExternalInteger(out, this.infoTable.size());
        for (Entry<UUID, ProcessHandleEntryDefinition> pair : this.infoTable.entrySet()) {
            UUIDUtil.writeExternal(out, pair.getKey());
            ObjectUtil.writeExternal(out, pair.getValue());
        }
    }
}
