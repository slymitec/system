package indi.sly.system.kernel.security.values;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.ArrayUtils;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.StringUtils;
import indi.sly.system.common.utility.UUIDUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "KernelGroups")
public class GroupEntity implements ISerializable<GroupEntity> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnDefinition = "uniqueidentifier", name = "ID", nullable = false, updatable = false)
    protected UUID id;
    @Column(length = 256, name = "Name", nullable = true)
    protected String name;
    @Column(length = 4096, name = "Token", nullable = false)
    protected byte[] token;

    public UUID getID() {
        return this.id;
    }

    public void setID(UUID id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getToken() {
        return this.token;
    }

    public void setToken(byte[] token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupEntity that = (GroupEntity) o;
        return id.equals(that.id) && name.equals(that.name) && Arrays.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name);
        result = 31 * result + Arrays.hashCode(token);
        return result;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public GroupEntity deepClone() {
        GroupEntity group = new GroupEntity();

        group.id = this.id;
        group.name = this.name;
        group.token = ArrayUtils.copyBytes(this.token);

        return group;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.id = UUIDUtils.readExternal(in);
        this.name = StringUtils.readExternal(in);
        this.token = NumberUtils.readExternalBytes(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtils.writeExternal(out, this.id);
        StringUtils.writeExternal(out, this.name);
        NumberUtils.writeExternalBytes(out, this.token);
    }
}


