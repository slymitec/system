package indi.sly.system.kernel.objects.entities;

import indi.sly.system.common.support.IDeepCloneable;
import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.StringUtils;
import indi.sly.system.common.utility.UUIDUtils;

import javax.persistence.*;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "KernelInfoRelations")
public class InfoRelationEntity implements IDeepCloneable<InfoRelationEntity>, ISerializable {
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
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.id = UUIDUtils.readExternal(in);
        this.parentID = UUIDUtils.readExternal(in);
        this.type = UUIDUtils.readExternal(in);
        this.name = StringUtils.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtils.writeExternal(out, this.id);
        UUIDUtils.writeExternal(out, this.parentID);
        UUIDUtils.writeExternal(out, this.type);
        StringUtils.writeExternal(out, this.name);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public InfoRelationEntity deepClone() {
        InfoRelationEntity infoRelation = new InfoRelationEntity();

        infoRelation.id = this.id;
        infoRelation.parentID = this.parentID;
        infoRelation.type = this.type;
        infoRelation.name = this.name;

        return infoRelation;
    }
}
