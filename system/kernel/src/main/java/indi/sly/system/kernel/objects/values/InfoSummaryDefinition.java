package indi.sly.system.kernel.objects.values;

import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.UUID;

public class InfoSummaryDefinition extends ADefinition<InfoSummaryDefinition> {
    private UUID id;
    private UUID type;
    private String name;

    public UUID getID() {
        return id;
    }

    public void setID(UUID id) {
        this.id = id;
    }

    public UUID getType() {
        return type;
    }

    public void setType(UUID type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfoSummaryDefinition that = (InfoSummaryDefinition) o;
        return Objects.equals(id, that.id) && Objects.equals(type, that.type) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, name);
    }

    @Override
    public InfoSummaryDefinition deepClone() {
        InfoSummaryDefinition definition = new InfoSummaryDefinition();

        definition.id = this.id;
        definition.type = this.type;
        definition.name = this.name;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        this.id = UUIDUtil.readExternal(in);
        this.type = UUIDUtil.readExternal(in);
        this.name = StringUtil.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        UUIDUtil.writeExternal(out, this.id);
        UUIDUtil.writeExternal(out, this.type);
        StringUtil.writeExternal(out, this.name);
    }
}
