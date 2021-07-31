package indi.sly.system.services.nativeinterface.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.services.nativeinterface.prototypes.NativeInterfaceMethodObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NativeInterfaceDefinition extends ADefinition<NativeInterfaceDefinition> {
    public NativeInterfaceDefinition() {
        this.methods = new HashMap<>();
    }

    private UUID id;
    private long attribute;
    private String name;
    private UUID processID;
    private final Map<String, NativeInterfaceMethodObject> methods;

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

    public Map<String, NativeInterfaceMethodObject> getMethods() {
        return this.methods;
    }
}
