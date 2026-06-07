package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.kernel.core.prototypes.AObject;

import java.util.Objects;
import java.util.UUID;

public class HandleContextDefinition extends ADefinition {
    public HandleContextDefinition(String clazz, UUID handle) {
        this.clazz = clazz;
        this.handle = handle;
    }

    private final String clazz;
    private final UUID handle;

    public String getClazz() {
        return this.clazz;
    }

    public UUID getHandle() {
        return this.handle;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof HandleContextDefinition that)) return false;
        return Objects.equals(clazz, that.clazz) && Objects.equals(handle, that.handle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, handle);
    }
}
