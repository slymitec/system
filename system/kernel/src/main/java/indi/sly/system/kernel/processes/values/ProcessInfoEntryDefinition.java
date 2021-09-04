package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.common.values.ADefinition;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;
import java.util.Map.Entry;

public class ProcessInfoEntryDefinition extends ADefinition<ProcessInfoEntryDefinition> {
    private UUID index;
    private final Map<Long, Long> date;
    private UUID id;
    private final List<IdentificationDefinition> identifications;
    private InfoOpenDefinition infoOpen;

    public ProcessInfoEntryDefinition() {
        this.date = new HashMap<>();
        this.identifications = new ArrayList<>();
    }

    public UUID getIndex() {
        return this.index;
    }

    public void setIndex(UUID index) {
        this.index = index;
    }

    public Map<Long, Long> getDate() {
        return this.date;
    }

    public UUID getID() {
        return this.id;
    }

    public void setID(UUID id) {
        this.id = id;
    }

    public List<IdentificationDefinition> getIdentifications() {
        return this.identifications;
    }

    public InfoOpenDefinition getInfoOpen() {
        return this.infoOpen;
    }

    public void setInfoOpen(InfoOpenDefinition infoOpen) {
        this.infoOpen = infoOpen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessInfoEntryDefinition that = (ProcessInfoEntryDefinition) o;
        return Objects.equals(index, that.index) && date.equals(that.date) && Objects.equals(id, that.id) && identifications.equals(that.identifications) && Objects.equals(infoOpen, that.infoOpen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, date, id, identifications, infoOpen);
    }

    @Override
    public ProcessInfoEntryDefinition deepClone() {
        ProcessInfoEntryDefinition definition = new ProcessInfoEntryDefinition();

        definition.index = this.index;
        definition.date.putAll(this.date);
        definition.id = this.id;
        definition.identifications.addAll(this.identifications);
        definition.infoOpen = this.infoOpen.deepClone();

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        this.index = UUIDUtil.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.date.put(NumberUtil.readExternalLong(in), NumberUtil.readExternalLong(in));
        }

        this.id = UUIDUtil.readExternal(in);

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.identifications.add(ObjectUtil.readExternal(in));
        }

        this.infoOpen = ObjectUtil.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        UUIDUtil.writeExternal(out, this.index);

        NumberUtil.writeExternalInteger(out, this.date.size());
        for (Entry<Long, Long> pair : this.date.entrySet()) {
            NumberUtil.writeExternalLong(out, pair.getKey());
            NumberUtil.writeExternalLong(out, pair.getValue());
        }

        UUIDUtil.writeExternal(out, this.id);

        NumberUtil.writeExternalInteger(out, this.identifications.size());
        for (IdentificationDefinition pair : this.identifications) {
            ObjectUtil.writeExternal(out, pair);
        }

        ObjectUtil.writeExternal(out, this.infoOpen);
    }
}
