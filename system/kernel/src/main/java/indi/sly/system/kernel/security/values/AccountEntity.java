package indi.sly.system.kernel.security.values;

import indi.sly.system.kernel.core.values.APersistentEntity;

import indi.sly.system.kernel.memory.repositories.prototypes.BinarySerializationAttributeConverterComponent;
import jakarta.persistence.*;

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
    @Basic(fetch = FetchType.LAZY)
    @Column(length = 4096, name = "Token", nullable = false)
    @Convert(converter = BinarySerializationAttributeConverterComponent.class)
    @Lob
    protected UserTokenEntity token;
    @Basic(fetch = FetchType.LAZY)
    @Column(length = 4096, name = "Sessions", nullable = false)
    @Convert(converter = BinarySerializationAttributeConverterComponent.class)
    @Lob
    protected AccountSessionsEntity sessions;

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

    public UserTokenEntity getToken() {
        return this.token;
    }

    public void setToken(UserTokenEntity token) {
        this.token = token;
    }

    public AccountSessionsEntity getSessions() {
        return this.sessions;
    }

    public void setSessions(AccountSessionsEntity sessions) {
        this.sessions = sessions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AccountEntity that = (AccountEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(password, that.password) && Objects.equals(groups, that.groups) && Objects.equals(token, that.token) && Objects.equals(sessions, that.sessions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, password, groups, token, sessions);
    }
}