package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.values.InfoStatusOpenDefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;
import java.util.Map.Entry;

public class ProcessHandleEntryDefinition implements ISerializeCapable<ProcessHandleEntryDefinition> {
    private final Map<Long, Long> date;
    private final List<IdentificationDefinition> identifications;
    private InfoStatusOpenDefinition open;

    public ProcessHandleEntryDefinition() {
        this.date = new HashMap<>();
        this.identifications = new ArrayList<>();
    }

    public Map<Long, Long> getDate() {
        return this.date;
    }

    public List<IdentificationDefinition> getIdentifications() {
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

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.date.put(NumberUtil.readExternalLong(in), NumberUtil.readExternalLong(in));
        }
        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.identifications.add(ObjectUtil.readExternal(in));
        }
        this.open = ObjectUtil.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        for (Entry<Long, Long> pair : this.date.entrySet()) {
            NumberUtil.writeExternalLong(out, pair.getKey());
            NumberUtil.writeExternalLong(out, pair.getValue());
        }

        NumberUtil.writeExternalInteger(out, this.identifications.size());
        for (IdentificationDefinition pair : this.identifications) {
            ObjectUtil.writeExternal(out, pair);
        }

        ObjectUtil.writeExternal(out, this.open);
    }
}
