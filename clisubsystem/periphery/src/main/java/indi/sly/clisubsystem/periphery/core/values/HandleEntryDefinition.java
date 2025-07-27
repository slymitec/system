package indi.sly.clisubsystem.periphery.core.values;

import indi.sly.clisubsystem.periphery.core.prototypes.AObject;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.UUID;

public class HandleEntryDefinition extends ADefinition<HandleEntryDefinition> {
    private UUID handle;
    private long space;
    private Class<? extends AObject> type;
    private UUID id;

    public UUID getHandle() {
        return this.handle;
    }

    public void setHandle(UUID handle) {
        this.handle = handle;
    }

    public long getSpace() {
        return this.space;
    }

    public void setSpace(long space) {
        this.space = space;
    }

    @SuppressWarnings("unchecked")
    public <T extends AObject> Class<T> getType() {
        return (Class<T>) this.type;
    }

    public <T extends AObject> void setType(Class<T> type) {
        this.type = type;
    }

    public UUID getID() {
        return this.id;
    }

    public void setID(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HandleEntryDefinition that = (HandleEntryDefinition) o;
        return space == that.space && Objects.equals(handle, that.handle) && Objects.equals(type, that.type) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(handle, space, type, id);
    }

    @Override
    public HandleEntryDefinition deepClone() {
        HandleEntryDefinition definition = new HandleEntryDefinition();

        definition.handle = this.handle;
        definition.space = this.space;
        definition.type = this.type;
        definition.id = this.id;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.handle = UUIDUtil.readExternal(in);
        this.space = NumberUtil.readExternalLong(in);
        this.type = ClassUtil.readExternal(in);
        this.id = UUIDUtil.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtil.writeExternal(out, this.handle);
        NumberUtil.writeExternalLong(out, this.space);
        ClassUtil.writeExternal(out, this.type);
        UUIDUtil.writeExternal(out, this.id);
    }
}
