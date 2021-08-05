package indi.sly.system.kernel.objects.values;

import indi.sly.system.common.supports.*;
import indi.sly.system.kernel.core.values.AEntity;

import javax.persistence.*;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "KernelInfos")
public class InfoEntity extends AEntity<InfoEntity> {
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

    public UUID getID() {
        return this.id;
    }

    public void setID(UUID id) {
        this.id = id;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfoEntity that = (InfoEntity) o;
        return id.equals(that.id) &&
                type.equals(that.type) &&
                occupied == that.occupied &&
                opened == that.opened &&
                Objects.equals(name, that.name) &&
                Arrays.equals(date, that.date) &&
                Arrays.equals(securityDescriptor, that.securityDescriptor) &&
                Arrays.equals(properties, that.properties) &&
                Arrays.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, type, occupied, opened, name);
        result = 31 * result + Arrays.hashCode(date);
        result = 31 * result + Arrays.hashCode(securityDescriptor);
        result = 31 * result + Arrays.hashCode(properties);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }

    @Override
    public InfoEntity deepClone() {
        InfoEntity entity = new InfoEntity();

        entity.id = this.id;
        entity.type = this.type;
        entity.occupied = this.occupied;
        entity.opened = this.opened;
        entity.name = this.name;
        entity.date = ArrayUtil.copyBytes(this.date);
        entity.securityDescriptor = ArrayUtil.copyBytes(this.securityDescriptor);
        entity.properties = ArrayUtil.copyBytes(this.properties);
        entity.content = ArrayUtil.copyBytes(this.content);

        return entity;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.id = UUIDUtil.readExternal(in);
        this.type = UUIDUtil.readExternal(in);
        this.occupied = NumberUtil.readExternalLong(in);
        this.opened = NumberUtil.readExternalLong(in);
        this.name = StringUtil.readExternal(in);
        this.date = NumberUtil.readExternalBytes(in);
        this.securityDescriptor = NumberUtil.readExternalBytes(in);
        this.properties = NumberUtil.readExternalBytes(in);
        this.content = NumberUtil.readExternalBytes(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtil.writeExternal(out, this.id);
        UUIDUtil.writeExternal(out, this.type);
        NumberUtil.writeExternalLong(out, this.occupied);
        NumberUtil.writeExternalLong(out, this.opened);
        StringUtil.writeExternal(out, this.name);
        NumberUtil.writeExternalBytes(out, this.date);
        NumberUtil.writeExternalBytes(out, this.securityDescriptor);
        NumberUtil.writeExternalBytes(out, this.properties);
        NumberUtil.writeExternalBytes(out, this.content);
    }
}
