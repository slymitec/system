package indi.sly.system.kernel.objects.values;

import indi.sly.system.common.values.ADefinition;

import java.util.UUID;

public class InfoSummaryDefinition extends ADefinition {
    private UUID id;
    private UUID type;
    private String name;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getType() {
        return type;
    }

    public void setType(UUID type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
