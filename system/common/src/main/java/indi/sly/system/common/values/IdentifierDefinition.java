package indi.sly.system.common.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public final class IdentifierDefinition extends ADefinition {
    private byte[] value;
    private Class<?> type;

    public byte[] getValue() {
        return this.value;
    }

    public Class<?> getType() {
        return this.type;
    }

    public IdentifierDefinition() {
        this.value = UUIDUtil.writeToBytes(UUIDUtil.getEmpty());
        this.type = UUID.class;
    }

    public IdentifierDefinition(UUID value) {
        if (ObjectUtil.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        this.value = UUIDUtil.writeToBytes(value);
        this.type = UUID.class;
    }

    public IdentifierDefinition(String value) {
        if (StringUtil.isNameIllegal(value)) {
            throw new ConditionParametersException();
        }

        this.value = StringUtil.writeToBytes(value);
        this.type = String.class;
    }

    @Override
    public String toString() {
        if (this.type == UUID.class) {
            return ObjectUtil.transferToString(UUIDUtil.readFormBytes(this.value));
        } else if (this.type == String.class) {
            return StringUtil.readFormBytes(this.value);
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IdentifierDefinition that)) return false;
        return Objects.deepEquals(value, that.value) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(value), type);
    }
}
