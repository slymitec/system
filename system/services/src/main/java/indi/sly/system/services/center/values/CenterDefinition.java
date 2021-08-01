package indi.sly.system.services.center.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.services.center.prototypes.processors.ACenterInitializer;

import java.util.UUID;

public class CenterDefinition extends ADefinition<CenterDefinition> {
    public CenterDefinition() {
    }

    private UUID id;
    private long attribute;
    private String name;
    private UUID processID;
    private ACenterInitializer initializer;

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

    public ACenterInitializer getInitializer() {
        return this.initializer;
    }

    public void setInitializer(ACenterInitializer initializer) {
        this.initializer = initializer;
    }
}
