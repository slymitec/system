package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.UUID;

public class ProcessSessionDefinition extends ADefinition {
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
        ProcessSessionDefinition that = (ProcessSessionDefinition) o;
        return link == that.link && Objects.equals(sessionID, that.sessionID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionID, link);
    }
}
