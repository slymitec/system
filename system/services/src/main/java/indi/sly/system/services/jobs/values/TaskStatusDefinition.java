package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskStatusDefinition extends ADefinition {
    public TaskStatusDefinition() {
        this.date = new HashMap<>();
    }

    private UUID handle;
    private final Map<Long, Long> date;
    private long runtime;

    public UUID getHandle() {
        return this.handle;
    }

    public void setHandle(UUID handle) {
        this.handle = handle;
    }

    public Map<Long, Long> getDate() {
        return this.date;
    }

    public long getRuntime() {
        return this.runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }
}
