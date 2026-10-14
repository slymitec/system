package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;

import java.util.UUID;

public class TaskDefinition extends ADefinition {
    @SuppressWarnings("NullAway.Init")
    private UUID id;
    private long attribute;
    @SuppressWarnings("NullAway.Init")
    private String name;
    @SuppressWarnings("NullAway.Init")
    private UUID processId;
    @SuppressWarnings("NullAway.Init")
    private ATaskInitializer initializer;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public long getAttribute() {
        return this.attribute;
    }

    public void setAttribute(long attribute) {
        this.attribute = attribute;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getProcessId() {
        return this.processId;
    }

    public void setProcessId(UUID processId) {
        this.processId = processId;
    }

    public ATaskInitializer getInitializer() {
        return this.initializer;
    }

    public void setInitializer(ATaskInitializer initializer) {
        this.initializer = initializer;
    }
}
