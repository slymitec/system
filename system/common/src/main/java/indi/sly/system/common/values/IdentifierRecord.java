package indi.sly.system.common.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.supports.*;
import jakarta.annotation.Nonnull;

import java.util.UUID;

public record IdentifierRecord(byte[] value, Class<?> type) {
    public IdentifierRecord() {
        this(UUIDUtil.writeToBytes(UUIDUtil.getEmpty()), UUID.class);
    }

    public IdentifierRecord(UUID value) {
        if (ObjectUtil.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        this(UUIDUtil.writeToBytes(value), UUID.class);
    }

    public IdentifierRecord(String value) {
        if (StringUtil.isNameIllegal(value)) {
            throw new ConditionParametersException();
        }

        this(StringUtil.writeToBytes(value), String.class);
    }

    @Override
    @Nonnull
    public String toString() {
        if (this.type == UUID.class) {
            return ObjectUtil.transferToString(UUIDUtil.readFormBytes(this.value));
        } else if (this.type == String.class) {
            return StringUtil.readFormBytes(this.value);
        } else {
            throw new StatusUnexpectedException();
        }
    }
}
