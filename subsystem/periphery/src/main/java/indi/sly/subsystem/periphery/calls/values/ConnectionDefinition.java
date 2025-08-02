package indi.sly.subsystem.periphery.calls.values;

import indi.sly.subsystem.periphery.calls.instances.prototypes.processors.AConnectionInitializer;
import indi.sly.system.common.values.ADefinition;

import java.util.UUID;

public class ConnectionDefinition extends ADefinition<ConnectionDefinition> {
    private UUID id;
    private long attribute;
    private String name;
    private String address;
    private AConnectionInitializer initializer;

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

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public AConnectionInitializer getInitializer() {
        return this.initializer;
    }

    public void setInitializer(AConnectionInitializer initializer) {
        this.initializer = initializer;
    }
}
