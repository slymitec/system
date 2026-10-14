package indi.sly.system.kernel.objects.values;

import org.jspecify.annotations.Nullable;

public record InfoOpenRecord(long attribute, byte @Nullable [] data) {
    public InfoOpenRecord withAttribute(long attribute) {
        return new InfoOpenRecord(attribute, this.data);
    }

    public InfoOpenRecord withData(byte[] data) {
        return new InfoOpenRecord(this.attribute, data);
    }
}
