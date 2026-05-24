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
    public final boolean equals(Object o) {
        if (!(o instanceof UserIdDefinition that)) return false;
        return type == that.type && Objects.equals(id, that.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id, type);
    }
}
