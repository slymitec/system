package indi.sly.system.kernel.files.instances.values;

import indi.sly.system.common.values.ADefinition;

import java.util.Arrays;
import java.util.Objects;

public class FileSystemEntryDefinition extends ADefinition {
    public FileSystemEntryDefinition() {
    }

    private long type;
    private byte[] value;

    public long getType() {
        return this.type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public byte[] getValue() {
        return this.value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof FileSystemEntryDefinition that)) return false;
        return type == that.type && Objects.deepEquals(value, that.value);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(type, Arrays.hashCode(value));
    }
}
