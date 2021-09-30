package indi.sly.system.kernel.processes.instances.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class SessionDefinition extends ADefinition<SessionDefinition> {
    public SessionDefinition() {
        this.processIDs = new HashSet<>();
        this.environmentVariables = new HashMap<>();
        this.parameters = new HashMap<>();
    }

    private String name;
    private long type;
    private UUID accountID;
    private final Set<UUID> processIDs;
    private final Map<String, String> environmentVariables;
    private final Map<String, String> parameters;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public Map<String, String> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionDefinition that = (SessionDefinition) o;
        return type == that.type && Objects.equals(name, that.name) && Objects.equals(accountID, that.accountID) && processIDs.equals(that.processIDs) && environmentVariables.equals(that.environmentVariables) && parameters.equals(that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, accountID, processIDs, environmentVariables, parameters);
    }

    @Override
    public SessionDefinition deepClone() {
        SessionDefinition definition = new SessionDefinition();

        definition.name = this.name;
        definition.type = this.type;
        definition.accountID = this.accountID;
        definition.processIDs.addAll(this.processIDs);
        definition.environmentVariables.putAll(this.environmentVariables);
        definition.parameters.putAll(this.parameters);

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        this.name = StringUtil.readExternal(in);
        this.type = NumberUtil.readExternalLong(in);
        this.accountID = UUIDUtil.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.processIDs.add(UUIDUtil.readExternal(in));
        }

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.environmentVariables.put(StringUtil.readExternal(in), StringUtil.readExternal(in));
        }

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.parameters.put(StringUtil.readExternal(in), StringUtil.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        StringUtil.writeExternal(out, this.name);
        NumberUtil.writeExternalLong(out, this.type);
        UUIDUtil.writeExternal(out, this.accountID);

        NumberUtil.writeExternalInteger(out, this.processIDs.size());
        for (UUID pair : this.processIDs) {
            UUIDUtil.writeExternal(out, pair);
        }

        NumberUtil.writeExternalInteger(out, this.environmentVariables.size());
        for (Map.Entry<String, String> pair : this.environmentVariables.entrySet()) {
            StringUtil.writeExternal(out, pair.getKey());
            StringUtil.writeExternal(out, pair.getValue());
        }

        NumberUtil.writeExternalInteger(out, this.parameters.size());
        for (Map.Entry<String, String> pair : this.parameters.entrySet()) {
            StringUtil.writeExternal(out, pair.getKey());
            StringUtil.writeExternal(out, pair.getValue());
        }
    }
}
