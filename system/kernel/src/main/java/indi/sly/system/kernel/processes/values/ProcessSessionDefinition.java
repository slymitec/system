package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.UUID;

public class ProcessSessionDefinition extends ADefinition<ProcessSessionDefinition> {
    private UUID accountID;
    private UUID sessionID;

    public UUID getAccountID() {
        return this.accountID;
    }

    public void setAccountID(UUID accountID) {
        this.accountID = accountID;
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
        return Objects.equals(accountID, that.accountID) && Objects.equals(sessionID, that.sessionID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountID, sessionID);
    }

    @Override
    public ProcessSessionDefinition deepClone() {
        ProcessSessionDefinition definition = new ProcessSessionDefinition();

        definition.accountID = this.accountID;
        definition.sessionID = this.sessionID;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        this.accountID = UUIDUtil.readExternal(in);
        this.sessionID = UUIDUtil.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        UUIDUtil.writeExternal(out, this.accountID);
        UUIDUtil.writeExternal(out, this.sessionID);
    }
}
