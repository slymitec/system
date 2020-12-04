package indi.sly.system.kernel.objects.values;

import java.util.UUID;

public class InfoSummaryDefinition {
    private UUID id;
    private UUID type;
    private String name;

    public UUID getID() {
        return id;
    }

    public void setID(UUID id) {
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
