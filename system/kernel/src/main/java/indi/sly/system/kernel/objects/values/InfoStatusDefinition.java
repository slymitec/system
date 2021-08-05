package indi.sly.system.kernel.objects.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;
import indi.sly.system.common.values.IdentificationDefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class InfoStatusDefinition extends ADefinition<InfoStatusDefinition> {
    private final List<IdentificationDefinition> identifications;
    private UUID parentID;

    public InfoStatusDefinition() {
        this.identifications = new ArrayList<>();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfoStatusDefinition that = (InfoStatusDefinition) o;
        return identifications.equals(that.identifications) &&
                Objects.equals(parentID, that.parentID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifications, parentID);
    }

    @Override
    public InfoStatusDefinition deepClone() {
        InfoStatusDefinition definition = new InfoStatusDefinition();

        definition.identifications.addAll(this.identifications);
        definition.parentID = this.parentID;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.identifications.add(ObjectUtil.readExternal(in));
        }
        this.parentID = UUIDUtil.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        NumberUtil.writeExternalInteger(out, this.identifications.size());
        for (IdentificationDefinition pair : this.identifications) {
            ObjectUtil.writeExternal(out, pair);
        }
        UUIDUtil.writeExternal(out, this.parentID);
    }
}
