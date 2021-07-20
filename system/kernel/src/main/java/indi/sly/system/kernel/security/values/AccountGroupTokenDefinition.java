package indi.sly.system.kernel.security.values;

import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.supports.NumberUtil;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class AccountGroupTokenDefinition implements ISerializeCapable<AccountGroupTokenDefinition> {
    public AccountGroupTokenDefinition() {
        this.limits = new HashMap<>();
    }

    private long privileges;
    private final Map<Long, Integer> limits;

    public long getPrivileges() {
        return this.privileges;
    }

    public void setPrivileges(long privileges) {
        this.privileges = privileges;
    }

    public Map<Long, Integer> getLimits() {
        return this.limits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountGroupTokenDefinition that = (AccountGroupTokenDefinition) o;
        return privileges == that.privileges && limits.equals(that.limits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(privileges, limits);
    }

    @Override
    public AccountGroupTokenDefinition deepClone() {
        AccountGroupTokenDefinition definition = new AccountGroupTokenDefinition();

        definition.privileges = this.privileges;
        definition.limits.putAll(this.limits);

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.privileges = NumberUtil.readExternalLong(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.limits.put(NumberUtil.readExternalLong(in), NumberUtil.readExternalInteger(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtil.writeExternalLong(out, this.privileges);

        NumberUtil.writeExternalInteger(out, this.limits.size());
        for (Map.Entry<Long, Integer> pair : this.limits.entrySet()) {
            NumberUtil.writeExternalLong(out, pair.getKey());
            NumberUtil.writeExternalInteger(out, pair.getValue());
        }
    }
}
