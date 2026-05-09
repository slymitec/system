package indi.sly.system.kernel.objects.values;

import indi.sly.system.kernel.core.values.APersistentEntity;

import jakarta.persistence.*;

import java.io.Serial;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "Kernel_Info_Relations")
public class InfoRelationEntity extends APersistentEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnDefinition = "uniqueidentifier", name = "ID", nullable = false, updatable = false)
    protected UUID id;
    @Column(columnDefinition = "uniqueidentifier", name = "ParentID", nullable = false, updatable = false)
    protected UUID parentId;
    @Column(columnDefinition = "uniqueidentifier", name = "Type", nullable = false)
    protected UUID type;
    @Column(length = 256, name = "Name", nullable = true)
    protected String name;

    public InfoRelationEntity() {
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getParentId() {
        return this.parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
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
        if (o == null || getClass() != o.getClass()) return false;
        InfoRelationEntity that = (InfoRelationEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(parentId, that.parentId) && Objects.equals(type, that.type) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parentId, type, name);
    }
}
