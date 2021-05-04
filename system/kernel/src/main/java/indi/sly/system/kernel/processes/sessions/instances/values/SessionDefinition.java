package indi.sly.system.kernel.processes.sessions.instances.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class SessionDefinition extends ADefinition<SessionDefinition> {
    public SessionDefinition() {
        this.processIDs = new HashSet<>();
    }

    private long type;
    private UUID accountID;
    private final Set<UUID> processIDs;
    private ClientDefinition client;

    public long getType() {
        return this.type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public UUID getAccountID() {
        return this.accountID;
    }

    public void setAccountID(UUID accountID) {
        this.accountID = accountID;
    }

    public Set<UUID> getProcessIDs() {
        return this.processIDs;
    }

    public ClientDefinition getClient() {
        return this.client;
    }

    public void setClient(ClientDefinition client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionDefinition that = (SessionDefinition) o;
        return type == that.type && Objects.equals(accountID, that.accountID) && processIDs.equals(that.processIDs) && Objects.equals(client, that.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, accountID, processIDs, client);
    }

    @Override
    public SessionDefinition deepClone() {
        SessionDefinition definition = new SessionDefinition();

        definition.type = this.type;
        definition.accountID = this.accountID;
        definition.processIDs.addAll(this.processIDs);
        definition.client = this.client.deepClone();

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.type = NumberUtil.readExternalLong(in);
        this.accountID = UUIDUtil.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.processIDs.add(UUIDUtil.readExternal(in));
        }

        this.client = ObjectUtil.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtil.writeExternalLong(out, this.type);
        UUIDUtil.writeExternal(out, this.accountID);

        NumberUtil.writeExternalInteger(out, this.processIDs.size());
        for (UUID pair : this.processIDs) {
            UUIDUtil.writeExternal(out, pair);
        }

        ObjectUtil.writeExternal(out, this.client);
    }
}
