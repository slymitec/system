package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;

import java.util.UUID;

public class ClientResponseExceptionDefinition extends ADefinition {
    private UUID id;
    private String clazz;
    private String ownerClazz;
    private String ownerMethod;
    private String message;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getOwnerClazz() {
        return ownerClazz;
    }

    public void setOwnerClazz(String ownerClazz) {
        this.ownerClazz = ownerClazz;
    }

    public String getOwnerMethod() {
        return this.ownerMethod;
    }

    public void setOwnerMethod(String ownerMethod) {
        this.ownerMethod = ownerMethod;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
