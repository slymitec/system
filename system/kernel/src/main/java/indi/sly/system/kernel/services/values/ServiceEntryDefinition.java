package indi.sly.system.kernel.services.values;

import indi.sly.system.common.values.ADefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServiceEntryDefinition extends ADefinition {
    public ServiceEntryDefinition() {
        this.dependencies = new ArrayList<>();
    }

    private final List<UUID> dependencies;
    private UUID processId;
    private long occupy;

    public List<UUID> getDependencies() {
        return this.dependencies;
    }

    public UUID getProcessId() {
        return this.processId;
    }

    public void setProcessId(UUID processId) {
        this.processId = processId;
    }

    public long getOccupy() {
        return occupy;
    }

    public void setOccupy(long occupy) {
        this.occupy = occupy;
    }
}
