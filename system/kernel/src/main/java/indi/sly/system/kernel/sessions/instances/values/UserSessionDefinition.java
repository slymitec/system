package indi.sly.system.kernel.sessions.instances.values;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class UserSessionDefinition implements ISerializable<UserSessionDefinition> {
    public UserSessionDefinition() {
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
        UserSessionDefinition that = (UserSessionDefinition) o;
        return type == that.type && Objects.equals(accountID, that.accountID) && processIDs.equals(that.processIDs) && Objects.equals(client, that.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, accountID, processIDs, client);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public UserSessionDefinition deepClone() {
        UserSessionDefinition definition = new UserSessionDefinition();

        definition.type = this.type;
        definition.accountID = this.accountID;
        definition.processIDs.addAll(this.processIDs);
        definition.client = this.client.deepClone();

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.type = NumberUtils.readExternalLong(in);
        this.accountID = UUIDUtils.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.processIDs.add(UUIDUtils.readExternal(in));
        }

        this.client = ObjectUtils.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtils.writeExternalLong(out, this.type);
        UUIDUtils.writeExternal(out, this.accountID);

        NumberUtils.writeExternalInteger(out, this.processIDs.size());
        for (UUID pair : this.processIDs) {
            UUIDUtils.writeExternal(out, pair);
        }

        ObjectUtils.writeExternal(out, this.client);
    }
}
