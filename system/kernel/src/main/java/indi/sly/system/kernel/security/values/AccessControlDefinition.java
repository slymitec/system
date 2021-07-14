package indi.sly.system.kernel.security.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.UUID;

public class AccessControlDefinition extends ADefinition<AccessControlDefinition> {
    private UUID id;
    private long type;
    private long scope;
    private long value;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public long getType() {
        return this.type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public long getScope() {
        return this.scope;
    }

    public void setScope(long scope) {
        this.scope = scope;
    }

    public long getValue() {
        return this.value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessControlDefinition that = (AccessControlDefinition) o;
        return type == that.type && scope == that.scope && value == that.value && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, scope, value);
    }

    @Override
    public AccessControlDefinition deepClone() {
        AccessControlDefinition definition = new AccessControlDefinition();

        definition.id = this.id;
        definition.type = this.type;
        definition.scope = this.scope;
        definition.value = this.value;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.id = UUIDUtil.readExternal(in);
        this.type = NumberUtil.readExternalLong(in);
        this.scope = NumberUtil.readExternalLong(in);
        this.value = NumberUtil.readExternalLong(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtil.writeExternal(out, this.id);
        NumberUtil.writeExternalLong(out, this.type);
        NumberUtil.writeExternalLong(out, this.scope);
        NumberUtil.writeExternalLong(out, this.value);
    }
}
