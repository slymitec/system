package indi.sly.system.kernel.security.entities;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "KernelAccounts")
public class AccountEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnDefinition = "uniqueidentifier", name = "ID", nullable = false, updatable = false)
    protected UUID id;
    @Column(length = 256, name = "Name", nullable = true)
    protected String name;
    @Column(length = 256, name = "password", nullable = true)
    protected String password;
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "KernelAccountsGroups", joinColumns = {@JoinColumn(name = "AccountID")}, inverseJoinColumns =
            {@JoinColumn(name = "GroupID")})
    protected List<GroupEntity> groups;

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
}
