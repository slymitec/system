package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.UUID;

public class ProcessSessionDefinition extends ADefinition<ProcessSessionDefinition> {
    private String accountName;
    private UUID sessionID;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
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
        return Objects.equals(accountName, that.accountName) && Objects.equals(sessionID, that.sessionID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountName, sessionID);
    }

    @Override
    public ProcessSessionDefinition deepClone() {
        ProcessSessionDefinition definition = new ProcessSessionDefinition();

        definition.accountName = this.accountName;
        definition.sessionID = this.sessionID;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        this.accountName = StringUtil.readExternal(in);
        this.sessionID = UUIDUtil.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        StringUtil.writeExternal(out, this.accountName);
        UUIDUtil.writeExternal(out, this.sessionID);
    }
}
