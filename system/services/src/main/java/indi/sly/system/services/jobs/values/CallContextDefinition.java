package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;

import java.util.UUID;

public class CallContextDefinition extends ADefinition {
    @SuppressWarnings("NullAway.Init")
    private UUID threadId;

    public UUID getThreadId() {
        return this.threadId;
    }

    public void setThreadId(UUID threadId) {
        this.threadId = threadId;
    }
}
