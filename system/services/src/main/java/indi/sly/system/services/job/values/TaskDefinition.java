package indi.sly.system.services.job.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.services.job.instances.prototypes.processors.ATaskInitializer;

import java.util.UUID;

public class TaskDefinition extends ADefinition<TaskDefinition> {
    public TaskDefinition() {
    }

    private UUID id;
    private long attribute;
    private String name;
    private UUID processID;
    private ATaskInitializer initializer;

    public UUID getID() {
        return this.id;
    }

    public void setID(UUID id) {
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

    public UUID getProcessID() {
        return this.processID;
    }

    public void setProcessID(UUID processID) {
        this.processID = processID;
    }

    public ATaskInitializer getInitializer() {
        return this.initializer;
    }

    public void setInitializer(ATaskInitializer initializer) {
        this.initializer = initializer;
    }
}
