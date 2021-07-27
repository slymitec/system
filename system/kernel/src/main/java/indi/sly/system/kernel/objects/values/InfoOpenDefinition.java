package indi.sly.system.kernel.objects.values;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.UUID;

import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

public class InfoOpenDefinition extends ADefinition<InfoOpenDefinition> {
    private UUID handle;
    private long attribute;
    private ISerializeCapable<?> context;

    public UUID getHandle() {
        return handle;
    }

    public void setHandle(UUID handle) {
        this.handle = handle;
    }

    public long getAttribute() {
        return this.attribute;
    }

    public void setAttribute(long openAttribute) {
        this.attribute = openAttribute;
    }

    public ISerializeCapable<?> getContext() {
        return this.context;
    }

    public void setContext(ISerializeCapable<?> context) {
        this.context = context;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfoOpenDefinition that = (InfoOpenDefinition) o;
        return attribute == that.attribute && Objects.equals(handle, that.handle) && Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(handle, attribute, context);
    }

    @Override
    public InfoOpenDefinition deepClone() {
        InfoOpenDefinition definition = new InfoOpenDefinition();

        definition.handle = this.handle;
        definition.attribute = this.attribute;
        definition.context = this.context;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.handle = UUIDUtil.readExternal(in);
        this.attribute = NumberUtil.readExternalLong(in);
        this.context = ObjectUtil.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtil.writeExternal(out, this.handle);
        NumberUtil.writeExternalLong(out, this.attribute);
        ObjectUtil.writeExternal(out, this.context);
    }
}
