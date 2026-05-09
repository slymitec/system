package indi.sly.system.kernel.security.values;

import indi.sly.system.common.supports.*;
import indi.sly.system.kernel.core.values.APersistentEntity;

import jakarta.persistence.*;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serial;
import java.util.*;

@Entity
@Table(name = "Kernel_Accounts")
public class AccountEntity extends APersistentEntity {
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
    @JoinTable(name = "Kernel_Accounts_Groups", joinColumns = {@JoinColumn(name = "AccountID")}, inverseJoinColumns =
            {@JoinColumn(name = "GroupID")})
    protected List<GroupEntity> groups;
    @Column(length = 4096, name = "Token", nullable = false)
    protected byte[] token;
    @Column(length = 4096, name = "Sessions", nullable = false)
    protected byte[] sessions;

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

    public byte[] getSessions() {
        return this.sessions;
    }

    public void setSessions(byte[] sessions) {
        this.sessions = sessions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AccountEntity that = (AccountEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(password, that.password) && Objects.equals(groups, that.groups) && Objects.deepEquals(token, that.token) && Objects.deepEquals(sessions, that.sessions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, password, groups, Arrays.hashCode(token), Arrays.hashCode(sessions));
    }
}