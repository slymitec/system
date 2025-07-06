package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.kernel.core.prototypes.AObject;

import java.util.UUID;

public class HandledObjectDefinition extends ADefinition<HandledObjectDefinition> {
    public HandledObjectDefinition() {
    }

    private Class<? extends AObject> type;
    private UUID handle;

    public Class<? extends AObject> getType() {
        return this.type;
    }

    public void setType(Class<? extends AObject> type) {
        this.type = type;
    }

    public UUID getHandle() {
        return this.handle;
    }

    public void setHandle(UUID handle) {
        this.handle = handle;
    }
}
