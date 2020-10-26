package indi.sly.system.kernel.processes.prototypes.instances;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.UUIDUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

public class PortDefinition implements ISerializable {
    private UUID fromDebug;
    private UUID fromException;
    private UUID toDebug;
    private UUID toException;

    public UUID getFromDebug() {
        return fromDebug;
    }

    public void setFromDebug(UUID fromDebug) {
        this.fromDebug = fromDebug;
    }

    public UUID getFromException() {
        return fromException;
    }

    public void setFromException(UUID fromException) {
        this.fromException = fromException;
    }

    public UUID getToDebug() {
        return toDebug;
    }

    public void setToDebug(UUID toDebug) {
        this.toDebug = toDebug;
    }

    public UUID getToException() {
        return toException;
    }

    public void setToException(UUID toException) {
        this.toException = toException;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        this.fromDebug = UUIDUtils.readExternal(in);
        this.fromException = UUIDUtils.readExternal(in);
        this.toDebug = UUIDUtils.readExternal(in);
        this.toException = UUIDUtils.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtils.writeExternal(out, this.fromDebug);
        UUIDUtils.writeExternal(out, this.fromException);
        UUIDUtils.writeExternal(out, this.toDebug);
        UUIDUtils.writeExternal(out, this.toException);
    }
}
