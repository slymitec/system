package indi.sly.system.kernel.objects.definitions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.objects.Identification;

public class InfoStatusDefinition implements ISerializable {
    private final List<Identification> identifications;
    private UUID parentID;
    private UUID handle;
    private InfoStatusOpenDefinition open;

    public InfoStatusDefinition() {
        this.identifications = new ArrayList<>();
        this.open = new InfoStatusOpenDefinition();
    }

    public List<Identification> getIdentifications() {
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

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.identifications.add(ObjectUtils.readExternal(in));
        }
        this.parentID = UUIDUtils.readExternal(in);
        this.open = ObjectUtils.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtils.writeExternalInteger(out, this.identifications.size());
        for (Identification pair : this.identifications) {
            ObjectUtils.writeExternal(out, pair);
        }
        UUIDUtils.writeExternal(out, this.parentID);
        ObjectUtils.writeExternal(out, this.open);
    }
}
