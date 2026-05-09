package indi.sly.system.kernel.objects.values;

import indi.sly.system.common.values.ADefinition;

public class InfoOpenDefinition extends ADefinition {
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
}
