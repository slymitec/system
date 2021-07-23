package indi.sly.system.kernel.security.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class AccessControlDefinition extends ADefinition<AccessControlDefinition> {
    public AccessControlDefinition() {
        this.userID = new UserIDDefinition();
    }

    private UserIDDefinition userID;
    private long scope;
    private long value;

    public UserIDDefinition getUserID() {
        return this.userID;
    }

    public long getScope() {
        return this.scope;
    }

    public void setScope(long scope) {
        this.scope = scope;
    }

    public long getValue() {
        return this.value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessControlDefinition that = (AccessControlDefinition) o;
        return scope == that.scope && value == that.value && userID.equals(that.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, scope, value);
    }

    @Override
    public AccessControlDefinition deepClone() {
        AccessControlDefinition definition = new AccessControlDefinition();

        definition.userID = this.userID.deepClone();
        definition.scope = this.scope;
        definition.value = this.value;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.userID = ObjectUtil.readExternal(in);
        this.scope = NumberUtil.readExternalLong(in);
        this.value = NumberUtil.readExternalLong(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        ObjectUtil.writeExternal(out, this.userID);
        NumberUtil.writeExternalLong(out, this.scope);
        NumberUtil.writeExternalLong(out, this.value);
    }
}
