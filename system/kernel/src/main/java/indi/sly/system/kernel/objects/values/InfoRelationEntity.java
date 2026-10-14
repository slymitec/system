package indi.sly.system.kernel.objects.values;

import indi.sly.system.kernel.core.values.APersistentEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "Kernel_Info_Relations")
public class InfoRelationEntity extends APersistentEntity {
    @SuppressWarnings("NullAway.Init")
    @Id
    @Column(name = "Id", nullable = false, updatable = false)
    protected UUID id;
    @SuppressWarnings("NullAway.Init")
    @Column(name = "ParentId", nullable = false, updatable = false)
    protected UUID parentId;
    @SuppressWarnings("NullAway.Init")
    @Column(name = "Type", nullable = false)
    protected UUID type;
    @Nullable
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

    public @Nullable String getName() {
        return this.name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof InfoRelationEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
