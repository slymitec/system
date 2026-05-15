package indi.sly.system.kernel.processes.values;

import indi.sly.system.kernel.core.values.APersistentEntity;

import java.util.Objects;
import java.util.UUID;

public class ProcessSessionEntity extends APersistentEntity {
    private UUID sessionID;
    private boolean link;

    public boolean isLink() {
        return this.link;
    }

    public void setLink(boolean link) {
        this.link = link;
    }

    public UUID getSessionID() {
        return this.sessionID;
    }

    public void setSessionID(UUID sessionID) {
        this.sessionID = sessionID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessSessionEntity that = (ProcessSessionEntity) o;
        return link == that.link && Objects.equals(sessionID, that.sessionID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionID, link);
    }
}
