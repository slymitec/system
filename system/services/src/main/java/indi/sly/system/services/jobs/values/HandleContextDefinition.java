package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.kernel.core.prototypes.AObject;

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
}
