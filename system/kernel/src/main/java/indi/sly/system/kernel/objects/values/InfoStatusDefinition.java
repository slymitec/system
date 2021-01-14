package indi.sly.system.kernel.objects.values;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.IdentificationDefinition;

public class InfoStatusDefinition implements ISerializeCapable {
    private final List<IdentificationDefinition> identifications;
    private UUID parentID;
    private UUID handle;
    private InfoStatusOpenDefinition open;

    public InfoStatusDefinition() {
        this.identifications = new ArrayList<>();
        this.open = new InfoStatusOpenDefinition();
    }

    public List<IdentificationDefinition> getIdentifications() {
        return this.identifications;
    }

    public UUID getParentID() {
        return this.parentID;
    }

    public void setParentID(UUID parentID) {
        this.parentID = parentID;
    }


    public UUID getHandle() {
        return handle;
    }

    public void setHandle(UUID handle) {
        this.handle = handle;
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
        InfoStatusDefinition that = (InfoStatusDefinition) o;
        return identifications.equals(that.identifications) &&
                Objects.equals(parentID, that.parentID) &&
                Objects.equals(handle, that.handle) &&
                open.equals(that.open);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifications, parentID, handle, open);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public InfoStatusDefinition deepClone() {
        InfoStatusDefinition definition = new InfoStatusDefinition();

        definition.identifications.addAll(this.identifications);
        definition.parentID = this.parentID;
        definition.handle = this.handle;
        definition.open = this.open.deepClone();

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.identifications.add(ObjectUtil.readExternal(in));
        }
        this.parentID = UUIDUtil.readExternal(in);
        this.open = ObjectUtil.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtil.writeExternalInteger(out, this.identifications.size());
        for (IdentificationDefinition pair : this.identifications) {
            ObjectUtil.writeExternal(out, pair);
        }
        UUIDUtil.writeExternal(out, this.parentID);
        ObjectUtil.writeExternal(out, this.open);
    }
}
