package indi.sly.system.common.values;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.supports.*;

public final class IdentificationDefinition implements ISerializeCapable<IdentificationDefinition> {
    private byte[] id;
    private Class<?> type;

    public byte[] getID() {
        return this.id;
    }

    public Class<?> getType() {
        return this.type;
    }

    public IdentificationDefinition() {
        this.id = UUIDUtil.writeToBytes(UUIDUtil.getEmpty());
        this.type = UUID.class;
    }

    public IdentificationDefinition(UUID id) {
        if (ObjectUtil.isAnyNull(id)) {
            throw new ConditionParametersException();
        }

        this.id = UUIDUtil.writeToBytes(id);
        this.type = UUID.class;
    }

    public IdentificationDefinition(String id) {
        if (StringUtil.isNameIllegal(id)) {
            throw new ConditionParametersException();
        }

        this.id = StringUtil.writeToBytes(id);
        this.type = String.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentificationDefinition that = (IdentificationDefinition) o;
        return Arrays.equals(id, that.id) && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(type);
        result = 31 * result + Arrays.hashCode(id);
        return result;
    }

    @Override
    public String toString() {
        if (this.type == UUID.class) {
            return UUIDUtil.toString(UUIDUtil.getFromBytes(this.id));
        } else if (this.type == String.class) {
            return StringUtil.readFormBytes(this.id);
        } else {
            return null;
        }
    }

    @Override
    public Object clone() {
        return this.deepClone();
    }

    @Override
    public IdentificationDefinition deepClone() {
        IdentificationDefinition definition = new IdentificationDefinition();

        definition.id = ArrayUtil.copyBytes(this.id);
        definition.type = this.type;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.id = NumberUtil.readExternalBytes(in);
        this.type = ClassUtil.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtil.writeExternalBytes(out, this.id);
        ClassUtil.writeExternal(out, this.type);
    }
}
