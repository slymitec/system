package indi.sly.system.kernel.files.instances.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Objects;

public class FileSystemEntryDefinition extends ADefinition<FileSystemEntryDefinition> {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileSystemEntryDefinition that = (FileSystemEntryDefinition) o;
        return type == that.type && Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(type);
        result = 31 * result + Arrays.hashCode(value);
        return result;
    }

    @Override
    public FileSystemEntryDefinition deepClone() {
        FileSystemEntryDefinition definition = new FileSystemEntryDefinition();

        definition.type = this.type;
        definition.value = this.value;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.type = NumberUtil.readExternalLong(in);
        this.value = NumberUtil.readExternalBytes(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtil.writeExternalLong(out, this.type);
        NumberUtil.writeExternalBytes(out, this.value);
    }
}
