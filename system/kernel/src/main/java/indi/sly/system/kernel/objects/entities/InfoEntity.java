package indi.sly.system.kernel.objects.entities;

import indi.sly.system.common.support.IDeepCloneable;
import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.StringUtils;
import indi.sly.system.common.utility.UUIDUtils;

import javax.persistence.*;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

@Entity
@Table(name = "KernelInfos")
public class InfoEntity implements IDeepCloneable<InfoEntity>, ISerializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnDefinition = "uniqueidentifier", name = "ID", nullable = false, updatable = false)
    protected UUID id;
    @Column(columnDefinition = "uniqueidentifier", name = "Type", nullable = false)
    protected UUID type;
    @Column(name = "Occupied", nullable = false)
    protected long occupied;
    @Column(name = "Opened", nullable = false)
    protected long opened;
    @Column(length = 256, name = "Name", nullable = true)
    protected String name;
    @Column(length = 256, name = "Date", nullable = false)
    protected byte[] date;
    @Column(length = 4096, name = "SecurityDescriptor", nullable = true)
    protected byte[] securityDescriptor;
    @Column(length = 1024, name = "Properties", nullable = false)
    protected byte[] properties;
    @Column(length = 4096, name = "ContentStream", nullable = true)
    protected byte[] content;

    public InfoEntity() {
    }

    public UUID getID() {
        return this.id;
    }

    public void setID(UUID objectID) {
        this.id = objectID;
    }

    public UUID getType() {
        return this.type;
    }

    public void setType(UUID type) {
        this.type = type;
    }

    public long getOccupied() {
        return this.occupied;
    }

    public void setOccupied(long occupied) {
        this.occupied = occupied;
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

    public byte[] getDate() {
        return this.date;
    }

    public void setDate(byte[] date) {
        this.date = date;
    }

    public byte[] getSecurityDescriptor() {
        return this.securityDescriptor;
    }

    public void setSecurityDescriptor(byte[] securityDescriptor) {
        this.securityDescriptor = securityDescriptor;
    }

    public byte[] getProperties() {
        return this.properties;
    }

    public void setProperties(byte[] properties) {
        this.properties = properties;
    }

    public byte[] getContent() {
        return this.content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        InfoEntity other = (InfoEntity) obj;
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
        this.type = UUIDUtils.readExternal(in);
        this.occupied = NumberUtils.readExternalLong(in);
        this.opened = NumberUtils.readExternalLong(in);
        this.name = StringUtils.readExternal(in);
        this.date = NumberUtils.readExternalBytes(in);
        this.securityDescriptor = NumberUtils.readExternalBytes(in);
        this.properties = NumberUtils.readExternalBytes(in);
        this.content = NumberUtils.readExternalBytes(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtils.writeExternal(out, this.id);
        UUIDUtils.writeExternal(out, this.type);
        NumberUtils.writeExternalLong(out, this.occupied);
        NumberUtils.writeExternalLong(out, this.opened);
        StringUtils.writeExternal(out, this.name);
        NumberUtils.writeExternalBytes(out, this.date);
        NumberUtils.writeExternalBytes(out, this.securityDescriptor);
        NumberUtils.writeExternalBytes(out, this.properties);
        NumberUtils.writeExternalBytes(out, this.content);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public InfoEntity deepClone() {
        InfoEntity infoEntity = new InfoEntity();

        infoEntity.id = this.id;
        infoEntity.type = this.type;
        infoEntity.occupied = this.occupied;
        infoEntity.opened = this.opened;
        infoEntity.name = this.name;
        if (ObjectUtils.allNotNull(this.date)) {
            infoEntity.date = new byte[this.date.length];
            System.arraycopy(this.date, 0, infoEntity.date, 0, this.date.length);
        }
        if (ObjectUtils.allNotNull(this.securityDescriptor)) {
            infoEntity.securityDescriptor = new byte[this.securityDescriptor.length];
            System.arraycopy(this.securityDescriptor, 0, infoEntity.securityDescriptor, 0, this.securityDescriptor.length);
        }
        if (ObjectUtils.allNotNull(this.properties)) {
            infoEntity.properties = new byte[this.properties.length];
            System.arraycopy(this.properties, 0, infoEntity.properties, 0, this.properties.length);
        }
        if (ObjectUtils.allNotNull(this.content)) {
            infoEntity.content = new byte[this.content.length];
            System.arraycopy(this.content, 0, infoEntity.content, 0, this.content.length);
        }

        return infoEntity;
    }
}
