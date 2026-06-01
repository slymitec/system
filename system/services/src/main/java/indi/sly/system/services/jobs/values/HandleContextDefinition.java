package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.kernel.core.prototypes.AObject;

import java.util.Objects;
import java.util.UUID;

public class HandleContextDefinition extends ADefinition {
    public HandleContextDefinition(String type, UUID handle) {
        this.type = type;
        this.handle = handle;
    }

    private final String type;
    private final UUID handle;

    public String getType() {
        return this.type;
    }

    public UUID getHandle() {
        return this.handle;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof HandleContextDefinition that)) return false;
        return Objects.equals(type, that.type) && Objects.equals(handle, that.handle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, handle);
    }
}
