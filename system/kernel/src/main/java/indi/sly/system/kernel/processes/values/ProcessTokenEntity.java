package indi.sly.system.kernel.processes.values;

import indi.sly.system.kernel.core.values.APersistentEntity;

import java.util.*;

public class ProcessTokenEntity extends APersistentEntity {
    public ProcessTokenEntity() {
        this.limits = new HashMap<>();
        this.roles = new HashSet<>();
    }

    private UUID accountId;
    private long privileges;
    private final Map<Long, Integer> limits;
    private final Set<UUID> roles;

    public UUID getAccountId() {
        return this.accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public long getPrivileges() {
        return this.privileges;
    }

    public void setPrivileges(long privileges) {
        this.privileges = privileges;
    }

    public Set<UUID> getRoles() {
        return this.roles;
    }

    public Map<Long, Integer> getLimits() {
        return this.limits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessTokenEntity that = (ProcessTokenEntity) o;
        return privileges == that.privileges &&
                Objects.equals(accountId, that.accountId) &&
                limits.equals(that.limits) &&
                roles.equals(that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, privileges, limits, roles);
    }
}
