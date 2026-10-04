package indi.sly.subsystem.periphery.proxies.instances.objects.values;

public record InfoOpenRecord(long attribute, byte[] data) {
    public InfoOpenRecord withAttribute(long attribute) {
        return new InfoOpenRecord(attribute, this.data);
    }

    public InfoOpenRecord withData(byte[] data) {
        return new InfoOpenRecord(this.attribute, data);
    }
}
