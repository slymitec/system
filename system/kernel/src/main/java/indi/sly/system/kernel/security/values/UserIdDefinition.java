package indi.sly.system.kernel.security.values;

import indi.sly.system.common.values.ADefinition;

import java.util.Objects;
import java.util.UUID;

public class UserIdDefinition extends ADefinition {
    public UserIdDefinition(UUID id, long type) {
        this.id = id;
        this.type = type;
    }

    private final UUID id;
    private final long type;

    public UUID getId() {
        return this.id;
    }

    public long getType() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserIdDefinition that = (UserIdDefinition) o;
        return type == that.type && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}
