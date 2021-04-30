package indi.sly.system.kernel.objects.values;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.ADefinition;

public class InfoStatusOpenDefinition extends ADefinition<InfoStatusOpenDefinition> {
    private long attribute;
    private ISerializeCapable<?> context;

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
        InfoStatusOpenDefinition that = (InfoStatusOpenDefinition) o;
        return attribute == that.attribute &&
                Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attribute, context);
    }

    @Override
    public InfoStatusOpenDefinition deepClone() {
        InfoStatusOpenDefinition definition = new InfoStatusOpenDefinition();

        definition.attribute = this.attribute;
        definition.context = this.context;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.attribute = NumberUtil.readExternalLong(in);
        this.context = ObjectUtil.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtil.writeExternalLong(out, this.attribute);
        ObjectUtil.writeExternal(out, this.context);
    }
}
