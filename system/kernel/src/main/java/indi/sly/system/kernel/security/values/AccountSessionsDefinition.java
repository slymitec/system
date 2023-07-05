package indi.sly.system.kernel.security.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class AccountSessionsDefinition extends ADefinition<AccountSessionsDefinition> {
    public AccountSessionsDefinition() {
        this.sessions = new HashSet<>();
    }

    private final Set<UUID> sessions;

    public Set<UUID> getSessions() {
        return this.sessions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountSessionsDefinition that = (AccountSessionsDefinition) o;
        return Objects.equals(sessions, that.sessions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessions);
    }

    @Override
    public AccountSessionsDefinition deepClone() {
        AccountSessionsDefinition definition = new AccountSessionsDefinition();

        definition.sessions.addAll(this.sessions);

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.sessions.add(UUIDUtil.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        NumberUtil.writeExternalInteger(out, this.sessions.size());
        for (UUID pair : this.sessions) {
            UUIDUtil.writeExternal(out, pair);
        }
    }
}
