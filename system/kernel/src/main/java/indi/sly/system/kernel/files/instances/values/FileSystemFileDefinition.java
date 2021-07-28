package indi.sly.system.kernel.files.instances.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

public class FileSystemFileDefinition extends ADefinition<FileSystemFileDefinition> {
    public FileSystemFileDefinition() {
    }

    private byte[] value;

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
        FileSystemFileDefinition that = (FileSystemFileDefinition) o;
        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public FileSystemFileDefinition deepClone() {
        FileSystemFileDefinition definition = new FileSystemFileDefinition();

        definition.value = this.value;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.value = NumberUtil.readExternalBytes(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtil.writeExternalBytes(out, this.value);
    }
}
