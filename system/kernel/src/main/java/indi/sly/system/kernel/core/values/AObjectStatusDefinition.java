package indi.sly.system.kernel.core.values;

import indi.sly.system.common.values.ADefinition;

import java.util.UUID;

public abstract class AObjectStatusDefinition<T> extends ADefinition<T> {
    private UUID handle;

    public final UUID getHandle() {
        return handle;
    }

    public final void setHandle(UUID handle) {
        this.handle = handle;
    }
}
