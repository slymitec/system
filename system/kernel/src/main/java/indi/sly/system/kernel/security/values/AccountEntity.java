package indi.sly.system.kernel.security.values;

import indi.sly.system.common.supports.*;
import indi.sly.system.kernel.core.values.AEntity;

import javax.persistence.*;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

@Entity
@Table(name = "KernelAccounts")
public class AccountEntity extends AEntity<AccountEntity> {
    private static final long serialVersionUID = 1L;

    public AccountEntity() {
        this.groups = new ArrayList<>();
    }

    @Id
    @Column(columnDefinition = "uniqueidentifier", name = "ID", nullable = false, updatable = false)
    protected UUID id;
    @Column(length = 256, name = "Name", nullable = false)
    protected String name;
    @Column(length = 256, name = "password", nullable = true)
    protected String password;
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "KernelAccountsGroups", joinColumns = {@JoinColumn(name = "AccountID")}, inverseJoinColumns =
            {@JoinColumn(name = "GroupID")})
    protected List<GroupEntity> groups;
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

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<GroupEntity> getGroups() {
        return this.groups;
    }

    public void setGroups(List<GroupEntity> groups) {
        this.groups = groups;
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
        AccountEntity that = (AccountEntity) o;
        return id.equals(that.id) && name.equals(that.name) && password.equals(that.password) && groups.equals(that.groups) && Arrays.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, password, groups);
        result = 31 * result + Arrays.hashCode(token);
        return result;
    }

    @Override
    public AccountEntity deepClone() {
        AccountEntity account = new AccountEntity();

        account.id = this.id;
        account.name = this.name;
        account.password = this.password;
        account.groups = this.groups;
        account.token = ArrayUtil.copyBytes(this.token);

        return account;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        int valueInteger;

        this.id = UUIDUtil.readExternal(in);
        this.name = StringUtil.readExternal(in);
        this.password = StringUtil.readExternal(in);

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.groups.add(ObjectUtil.readExternal(in));
        }

        this.token = NumberUtil.readExternalBytes(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        UUIDUtil.writeExternal(out, this.id);
        StringUtil.writeExternal(out, this.name);
        StringUtil.writeExternal(out, this.password);

        NumberUtil.writeExternalInteger(out, this.groups.size());
        for (GroupEntity pair : this.groups) {
            ObjectUtil.writeExternal(out, pair);
        }

        NumberUtil.writeExternalBytes(out, this.token);
    }
}
