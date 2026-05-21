package indi.sly.system.kernel.security.values;

import indi.sly.system.kernel.core.values.APersistentEntity;

import indi.sly.system.kernel.memory.repositories.prototypes.BinarySerializationAttributeConverterComponent;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "Kernel_Groups")
public class GroupEntity extends APersistentEntity {
    @Id
    @Column(columnDefinition = "uniqueidentifier", name = "Id", nullable = false, updatable = false)
    protected UUID id;
    @Column(length = 256, name = "Name", nullable = false)
    protected String name;
    @Basic(fetch = FetchType.LAZY)
    @Column(length = 4096, name = "Token", nullable = false)
    @Convert(converter = BinarySerializationAttributeConverterComponent.class)
    @Lob
    protected UserTokenEntity token;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserTokenEntity getToken() {
        return this.token;
    }

    public void setToken(UserTokenEntity token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GroupEntity that = (GroupEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, token);
    }
}