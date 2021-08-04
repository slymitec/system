package indi.sly.system.kernel.objects.values;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Objects;

import indi.sly.system.common.supports.ArrayUtil;
import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.values.ADefinition;

public class InfoOpenDefinition extends ADefinition<InfoOpenDefinition> {
    private long attribute;
    private byte[] data;

    public long getAttribute() {
        return this.attribute;
    }

    public void setAttribute(long openAttribute) {
        this.attribute = openAttribute;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfoOpenDefinition that = (InfoOpenDefinition) o;
        return attribute == that.attribute && Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(attribute);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public InfoOpenDefinition deepClone() {
        InfoOpenDefinition definition = new InfoOpenDefinition();

        definition.attribute = this.attribute;
        definition.data = ArrayUtil.copyBytes(this.data);

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.attribute = NumberUtil.readExternalLong(in);
        this.data = NumberUtil.readExternalBytes(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtil.writeExternalLong(out, this.attribute);
        NumberUtil.writeExternalBytes(out, this.data);
    }
}
