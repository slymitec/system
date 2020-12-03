package indi.sly.system.kernel.objects.entities;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.*;

import javax.persistence.*;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "KernelInfos")
public class InfoEntity implements ISerializable<InfoEntity> {
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
    protected Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public InfoEntity deepClone() {
        InfoEntity entity = new InfoEntity();

        entity.id = this.id;
        entity.type = this.type;
        entity.occupied = this.occupied;
        entity.opened = this.opened;
        entity.name = this.name;
        entity.date = ArrayUtils.copyBytes(this.date);
        entity.securityDescriptor = ArrayUtils.copyBytes(this.securityDescriptor);
        entity.properties = ArrayUtils.copyBytes(this.properties);
        entity.content = ArrayUtils.copyBytes(this.content);

        return entity;
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
}
