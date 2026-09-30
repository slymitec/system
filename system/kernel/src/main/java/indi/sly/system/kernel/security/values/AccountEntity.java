package indi.sly.system.kernel.security.values;

import indi.sly.system.kernel.core.values.APersistentEntity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

@Entity
@Table(name = "Kernel_Accounts")
public class AccountEntity extends APersistentEntity {
    public AccountEntity() {
        this.groups = new ArrayList<>();
    }

    @Id
    @Column(columnDefinition = "uniqueidentifier", name = "Id", nullable = false, updatable = false)
    protected UUID id;
    @Column(length = 256, name = "Name", nullable = false)
    protected String name;
    @Column(length = 256, name = "password", nullable = true)
    protected String password;
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "Kernel_Accounts_Groups", joinColumns = {@JoinColumn(name = "AccountId")}, inverseJoinColumns = {@JoinColumn(name = "GroupId")})
    protected List<GroupEntity> groups;
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "json", name = "Token", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    protected UserTokenEntity token;
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "json", name = "Sessions", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
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
        if (!(o instanceof AccountEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}