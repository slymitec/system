package indi.sly.system.kernel.objects.values;

import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.kernel.core.values.AEntity;

import javax.persistence.*;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "KernelInfoRelations")
public class InfoRelationEntity extends AEntity<InfoRelationEntity> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnDefinition = "uniqueidentifier", name = "ID", nullable = false, updatable = false)
    protected UUID id;
    @Column(columnDefinition = "uniqueidentifier", name = "ParentID", nullable = false, updatable = false)
    protected UUID parentID;
    @Column(columnDefinition = "uniqueidentifier", name = "Type", nullable = false)
    protected UUID type;
    @Column(length = 256, name = "Name", nullable = true)
    protected String name;

    public InfoRelationEntity() {
    }

    public UUID getID() {
        return this.id;
    }

    public void setID(UUID id) {
        this.id = id;
    }

    public UUID getParentID() {
        return this.parentID;
    }

    public void setParentID(UUID parentID) {
        this.parentID = parentID;
    }

    public UUID getType() {
        return this.type;
    }

    public void setType(UUID type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfoRelationEntity that = (InfoRelationEntity) o;
        return id.equals(that.id) &&
                parentID.equals(that.parentID) &&
                type.equals(that.type) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parentID, type, name);
    }

    @Override
    public InfoRelationEntity deepClone() {
        InfoRelationEntity entity = new InfoRelationEntity();

        entity.id = this.id;
        entity.parentID = this.parentID;
        entity.type = this.type;
        entity.name = this.name;

        return entity;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.id = UUIDUtil.readExternal(in);
        this.parentID = UUIDUtil.readExternal(in);
        this.type = UUIDUtil.readExternal(in);
        this.name = StringUtil.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtil.writeExternal(out, this.id);
        UUIDUtil.writeExternal(out, this.parentID);
        UUIDUtil.writeExternal(out, this.type);
        StringUtil.writeExternal(out, this.name);
    }
}
