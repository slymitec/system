package indi.sly.system.common.values;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.*;

public final class Identification implements ISerializable<Identification> {
    private byte[] id;
    private Class<?> type;

    public byte[] getID() {
        return this.id;
    }

    public Class<?> getType() {
        return this.type;
    }

    public Identification() {
        this.id = UUIDUtils.writeToBytes(UUIDUtils.getEmpty());
        this.type = UUID.class;
    }

    public Identification(UUID id) {
        if (ObjectUtils.isAnyNull(id)) {
            throw new ConditionParametersException();
        }

        this.id = UUIDUtils.writeToBytes(id);
        this.type = UUID.class;
    }

    public Identification(String id) {
        if (StringUtils.isNameIllegal(id)) {
            throw new ConditionParametersException();
        }

        this.id = StringUtils.writeToBytes(id);
        this.type = String.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identification that = (Identification) o;
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
        return ClassUtils.getSimpleName(Identification.class);
    }

    @Override
    public Object clone() {
        return this.deepClone();
    }

    @Override
    public Identification deepClone() {
        Identification definition = new Identification();

        definition.id = ArrayUtils.copyBytes(this.id);
        definition.type = this.type;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.id = NumberUtils.readExternalBytes(in);
        this.type = ClassUtils.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtils.writeExternalBytes(out, this.id);
        ClassUtils.writeExternal(out, this.type);
    }
}
