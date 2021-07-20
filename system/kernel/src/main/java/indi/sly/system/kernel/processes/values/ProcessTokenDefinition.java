package indi.sly.system.kernel.processes.values;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

public class ProcessTokenDefinition extends ADefinition<ProcessTokenDefinition> {
    public ProcessTokenDefinition() {
        this.roles = new HashSet<>();
        this.limits = new HashMap<>();
    }

    private UUID accountID;
    private long privileges;
    private final Map<Long, Integer> limits;
    private final Set<UUID> roles;

    public UUID getAccountID() {
        return this.accountID;
    }

    public void setAccountID(UUID accountID) {
        this.accountID = accountID;
    }

    public long getPrivileges() {
        return this.privileges;
    }

    public void setPrivileges(long privileges) {
        this.privileges = privileges;
    }

    public Set<UUID> getRoles() {
        return this.roles;
    }

    public Map<Long, Integer> getLimits() {
        return this.limits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessTokenDefinition that = (ProcessTokenDefinition) o;
        return privileges == that.privileges &&
                Objects.equals(accountID, that.accountID) &&
                limits.equals(that.limits) &&
                roles.equals(that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountID, privileges, limits, roles);
    }

    @Override
    public ProcessTokenDefinition deepClone() {
        ProcessTokenDefinition definition = new ProcessTokenDefinition();

        definition.accountID = this.accountID;
        definition.privileges = this.privileges;
        definition.limits.putAll(this.limits);
        definition.roles.addAll(this.roles);

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.accountID = UUIDUtil.readExternal(in);
        this.privileges = NumberUtil.readExternalLong(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.limits.put(NumberUtil.readExternalLong(in), NumberUtil.readExternalInteger(in));
        }

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.roles.add(UUIDUtil.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtil.writeExternal(out, this.accountID);
        NumberUtil.writeExternalLong(out, this.privileges);

        NumberUtil.writeExternalInteger(out, this.limits.size());
        for (Map.Entry<Long, Integer> pair : this.limits.entrySet()) {
            NumberUtil.writeExternalLong(out, pair.getKey());
            NumberUtil.writeExternalInteger(out, pair.getValue());
        }

        NumberUtil.writeExternalInteger(out, this.roles.size());
        for (UUID pair : this.roles) {
            UUIDUtil.writeExternal(out, pair);
        }
    }
}
