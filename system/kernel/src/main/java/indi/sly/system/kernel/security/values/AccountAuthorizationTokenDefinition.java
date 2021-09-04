package indi.sly.system.kernel.security.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

public class AccountAuthorizationTokenDefinition extends ADefinition<AccountAuthorizationTokenDefinition> {
    public AccountAuthorizationTokenDefinition() {
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
    public AccountAuthorizationTokenDefinition deepClone() {
        AccountAuthorizationTokenDefinition definition = new AccountAuthorizationTokenDefinition();

        definition.privileges = this.privileges;
        definition.limits.putAll(this.limits);

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

    }
}
