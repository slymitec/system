package indi.sly.system.kernel.security.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class AccountAuthorizationTokenDefinition extends ADefinition<AccountAuthorizationTokenDefinition> {
    public AccountAuthorizationTokenDefinition() {
        this.limits = new HashMap<>();
        this.roles = new HashSet<>();
    }

    private long privileges;
    private final Map<Long, Integer> limits;
    private final Set<UUID> roles;

    public long getPrivileges() {
        return this.privileges;
    }

    public void setPrivileges(long privileges) {
        this.privileges = privileges;
    }

    public Map<Long, Integer> getLimits() {
        return this.limits;
    }

    public Set<UUID> getRoles() {
        return this.roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountAuthorizationTokenDefinition that = (AccountAuthorizationTokenDefinition) o;
        return privileges == that.privileges && limits.equals(that.limits) && roles.equals(that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(privileges, limits, roles);
    }

    @Override
    public AccountAuthorizationTokenDefinition deepClone() {
        AccountAuthorizationTokenDefinition definition = new AccountAuthorizationTokenDefinition();

        definition.privileges = this.privileges;
        definition.limits.putAll(this.limits);
        definition.roles.addAll(this.roles);

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

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
        super.writeExternal(out);

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
