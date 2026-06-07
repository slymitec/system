package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;

import java.util.UUID;

public class UserContentResponseDefinition extends ADefinition {
    private UUID id;
    private String clazz;
    private String value;

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

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
