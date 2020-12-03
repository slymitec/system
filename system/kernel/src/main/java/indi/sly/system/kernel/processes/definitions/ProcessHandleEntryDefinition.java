package indi.sly.system.kernel.processes.definitions;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.objects.definitions.InfoStatusOpenDefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;
import java.util.Map.Entry;

public class ProcessHandleEntryDefinition implements ISerializable<ProcessHandleEntryDefinition> {
    private final Map<Long, Long> date;
    private final List<Identification> identifications;
    private InfoStatusOpenDefinition open;

    public ProcessHandleEntryDefinition() {
        this.date = new HashMap<>();
        this.identifications = new ArrayList<>();
    }

    public Map<Long, Long> getDate() {
        return this.date;
    }

    public List<Identification> getIdentifications() {
        return this.identifications;
    }

    public InfoStatusOpenDefinition getOpen() {
        return open;
    }

    public void setOpen(InfoStatusOpenDefinition open) {
        this.open = open;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessHandleEntryDefinition that = (ProcessHandleEntryDefinition) o;
        return date.equals(that.date) &&
                identifications.equals(that.identifications) &&
                Objects.equals(open, that.open);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, identifications, open);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public ProcessHandleEntryDefinition deepClone() {
        ProcessHandleEntryDefinition definition = new ProcessHandleEntryDefinition();

        definition.date.putAll(this.date);
        definition.identifications.addAll(this.identifications);
        definition.open = this.open.deepClone();

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int valueInteger;

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.date.put(NumberUtils.readExternalLong(in), NumberUtils.readExternalLong(in));
        }
        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.identifications.add(ObjectUtils.readExternal(in));
        }
        this.open = ObjectUtils.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        for (Entry<Long, Long> pair : this.date.entrySet()) {
            NumberUtils.writeExternalLong(out, pair.getKey());
            NumberUtils.writeExternalLong(out, pair.getValue());
        }

        NumberUtils.writeExternalInteger(out, this.identifications.size());
        for (Identification pair : this.identifications) {
            ObjectUtils.writeExternal(out, pair);
        }

        ObjectUtils.writeExternal(out, this.open);
    }
}
