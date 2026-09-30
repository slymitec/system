package indi.sly.system.kernel.objects.values;

import indi.sly.system.kernel.core.values.APersistentEntity;
import indi.sly.system.kernel.security.values.SecurityDescriptorEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "Kernel_Infos")
public class InfoEntity extends APersistentEntity {
    @Id
    @Column(name = "Id", nullable = false, updatable = false)
    protected UUID id;
    @Column(name = "Type", nullable = false)
    protected UUID type;
    @Column(name = "Opened", nullable = false)
    protected long opened;
    @Column(length = 256, name = "Name", nullable = true)
    protected String name;
    @Column(columnDefinition = "json", name = "Date", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    protected Map<Long, Long> date;
    @Column(columnDefinition = "json", name = "Security_Descriptor", nullable = true)
    @JdbcTypeCode(SqlTypes.JSON)
    protected SecurityDescriptorEntity securityDescriptor;
    @Column(columnDefinition = "json", name = "Properties", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    protected Map<String, String> properties;
    @Basic(fetch = FetchType.LAZY)
    @Column(length = 4096, name = "Content_Stream", nullable = true)
    @JdbcTypeCode(SqlTypes.VARBINARY)
    protected byte[] content;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getType() {
        return this.type;
    }

    public void setType(UUID type) {
        this.type = type;
    }

    public long getOpened() {
        return this.opened;
    }

    public void setOpened(long opened) {
        this.opened = opened;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Long, Long> getDate() {
        return this.date;
    }

    public void setDate(Map<Long, Long> date) {
        this.date = date;
    }

    public SecurityDescriptorEntity getSecurityDescriptor() {
        return this.securityDescriptor;
    }

    public void setSecurityDescriptor(SecurityDescriptorEntity securityDescriptor) {
        this.securityDescriptor = securityDescriptor;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public byte[] getContent() {
        return this.content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof InfoEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
