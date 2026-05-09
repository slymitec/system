package indi.sly.system.kernel.processes.instances.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class SessionDefinition extends ADefinition {
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
        return type == that.type && Objects.equals(name, that.name) && Objects.equals(accountID, that.accountID) && Objects.equals(processIDs, that.processIDs) && Objects.equals(environmentVariables, that.environmentVariables) && Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, accountID, processIDs, environmentVariables, parameters);
    }
}
