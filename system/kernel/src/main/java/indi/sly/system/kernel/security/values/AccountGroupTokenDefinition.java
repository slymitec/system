package indi.sly.system.kernel.security.values;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AccountGroupTokenDefinition implements ISerializable<AccountGroupTokenDefinition> {
    public AccountGroupTokenDefinition() {
        this.limits = new HashMap<>();
    }

    private long privilegeTypes;
    private final Map<Long, Integer> limits;

    public long getPrivilegeTypes() {
        return this.privilegeTypes;
    }

    public void setPrivilegeTypes(long privilegeTypes) {
        this.privilegeTypes = privilegeTypes;
    }

    public Map<Long, Integer> getLimits() {
        return this.limits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountGroupTokenDefinition that = (AccountGroupTokenDefinition) o;
        return privilegeTypes == that.privilegeTypes && limits.equals(that.limits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(privilegeTypes, limits);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public AccountGroupTokenDefinition deepClone() {
        AccountGroupTokenDefinition definition = new AccountGroupTokenDefinition();

        definition.privilegeTypes = this.privilegeTypes;
        definition.limits.putAll(this.limits);

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.privilegeTypes = NumberUtils.readExternalLong(in);

        int valueInteger;

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.limits.put(NumberUtils.readExternalLong(in), NumberUtils.readExternalInteger(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtils.writeExternalLong(out, this.privilegeTypes);

        NumberUtils.writeExternalInteger(out, this.limits.size());
        for (Map.Entry<Long, Integer> pair : this.limits.entrySet()) {
            NumberUtils.writeExternalLong(out, pair.getKey());
            NumberUtils.writeExternalInteger(out, pair.getValue());
        }
    }
}
