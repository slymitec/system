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
    public final boolean equals(Object o) {
        if (!(o instanceof AccessControlDefinition that)) return false;
        return scope == that.scope && value == that.value && Objects.equals(userId, that.userId);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(userId, scope, value);
    }
}
