package indi.sly.system.kernel.objects.entities;

import indi.sly.system.common.support.IDeepCloneable;
import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.StringUtils;
import indi.sly.system.common.utility.UUIDUtils;

import javax.persistence.*;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
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
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        InfoRelationEntity other = (InfoRelationEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
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
        InfoRelationEntity InfoRelationEntity = new InfoRelationEntity();

        InfoRelationEntity.id = this.id;
        InfoRelationEntity.parentID = this.parentID;
        InfoRelationEntity.type = this.type;
        InfoRelationEntity.name = this.name;

        return InfoRelationEntity;
    }
}
