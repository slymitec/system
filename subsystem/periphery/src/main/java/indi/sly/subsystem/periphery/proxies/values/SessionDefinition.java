package indi.sly.subsystem.periphery.proxies.values;

import indi.sly.system.common.values.ADefinition;

import java.util.UUID;

public class SessionDefinition extends ADefinition {
    public SessionDefinition() {}

    private UUID sessionId;

    public UUID getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }
}
