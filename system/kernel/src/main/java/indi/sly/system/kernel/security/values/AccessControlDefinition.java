package indi.sly.system.kernel.security.values;

import indi.sly.system.common.values.ADefinition;

import java.util.Objects;

public class AccessControlDefinition extends ADefinition {
    private UserIdDefinition userId;
    private long scope;
    private long value;

    public UserIdDefinition getUserId() {
        return this.userId;
    }

    public void setUserId(UserIdDefinition userId) {
        this.userId = userId;
    }

    public long getScope() {
        return this.scope;
    }

    public void setScope(long scope) {
        this.scope = scope;
    }

    public long getValue() {
        return this.value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessControlDefinition that = (AccessControlDefinition) o;
        return scope == that.scope && value == that.value && userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, scope, value);
    }
}
