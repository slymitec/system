package indi.sly.system.kernel.processes.instances.values;

import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.UUID;

public class HandlerEntryDefinition extends ADefinition<HandlerEntryDefinition> {
    public HandlerEntryDefinition() {
    }

    private UUID handlerID;
    private UUID kernelID;

    public UUID getHandlerID() {
        return this.handlerID;
    }

    public void setHandlerID(UUID handlerID) {
        this.handlerID = handlerID;
    }

    public UUID getKernelID() {
        return this.kernelID;
    }

    public void setKernelID(UUID kernelID) {
        this.kernelID = kernelID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HandlerEntryDefinition that = (HandlerEntryDefinition) o;
        return Objects.equals(handlerID, that.handlerID) && Objects.equals(kernelID, that.kernelID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(handlerID, kernelID);
    }

    @Override
    public HandlerEntryDefinition deepClone() {
        HandlerEntryDefinition definition = new HandlerEntryDefinition();

        definition.handlerID = handlerID;
        definition.kernelID = kernelID;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        this.handlerID = UUIDUtil.readExternal(in);
        this.kernelID = UUIDUtil.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        UUIDUtil.writeExternal(out, this.handlerID);
        UUIDUtil.writeExternal(out, this.kernelID);
    }
}
