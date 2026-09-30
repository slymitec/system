package indi.sly.system.kernel.security.values;

import indi.sly.system.kernel.core.values.APersistentEntity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "Kernel_Groups")
public class GroupEntity extends APersistentEntity {
    @Id
    @Column(name = "Id", nullable = false, updatable = false)
    protected UUID id;
    @Column(length = 256, name = "Name", nullable = false)
    protected String name;
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "json", name = "Token", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
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
        if (!(o instanceof GroupEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}