package indi.sly.system.kernel.processes.values;

import indi.sly.system.kernel.core.values.APersistentEntity;

import java.util.Objects;
import java.util.UUID;

public class ProcessSessionEntity extends APersistentEntity {
    private UUID sessionId;
    private long type;

    public long getType() {
        return this.type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public UUID getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProcessSessionEntity that)) return false;
        return type == that.type && Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, type);
    }
}
