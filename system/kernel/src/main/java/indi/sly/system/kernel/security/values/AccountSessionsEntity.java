package indi.sly.system.kernel.security.values;

import indi.sly.system.kernel.core.values.APersistentEntity;

import java.util.*;

public class AccountSessionsEntity extends APersistentEntity {
    public AccountSessionsEntity() {
        this.sessions = new HashSet<>();
    }

    private final Set<UUID> sessions;

    public Set<UUID> getSessions() {
        return this.sessions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AccountSessionsEntity that = (AccountSessionsEntity) o;
        return Objects.equals(sessions, that.sessions);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sessions);
    }
}
