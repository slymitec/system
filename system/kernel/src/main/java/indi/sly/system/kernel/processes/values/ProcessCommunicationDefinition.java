package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.supports.*;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class ProcessCommunicationDefinition extends ADefinition {
    public ProcessCommunicationDefinition() {
        this.shared = ArrayUtil.EMPTY_BYTES;
        this.portIDs = new HashSet<>();
    }

    private byte[] shared;
    private final Set<UUID> portIDs;
    private UUID signalID;

    public byte[] getShared() {
        return this.shared;
    }

    public void setShared(byte[] shared) {
        this.shared = shared;
    }

    public Set<UUID> getPortIDs() {
        return this.portIDs;
    }

    public UUID getSignalID() {
        return this.signalID;
    }

    public void setSignalID(UUID signalID) {
        this.signalID = signalID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessCommunicationDefinition that = (ProcessCommunicationDefinition) o;
        return Arrays.equals(shared, that.shared) && portIDs.equals(that.portIDs) && Objects.equals(signalID,
                that.signalID);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(portIDs, signalID);
        result = 31 * result + Arrays.hashCode(shared);
        return result;
    }
}
