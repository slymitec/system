package indi.sly.system.kernel.security.values;

import indi.sly.system.kernel.core.values.APersistentEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserTokenEntity extends APersistentEntity {
    public UserTokenEntity() {
        this.limits = new HashMap<>();
    }

    private long privileges;
    private final Map<Long, Integer> limits;

    public long getPrivileges() {
        return this.privileges;
    }

    public void setPrivileges(long privileges) {
        this.privileges = privileges;
    }

    public Map<Long, Integer> getLimits() {
        return this.limits;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserTokenEntity that = (UserTokenEntity) o;
        return privileges == that.privileges && Objects.equals(limits, that.limits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(privileges, limits);
    }
}
