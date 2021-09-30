package indi.sly.system.kernel.security.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class UserSessionDefinition extends ADefinition<UserSessionDefinition> {
    public UserSessionDefinition() {
        this.ids = new HashMap<>();
    }

    private long limit;
    private final Map<String, UUID> ids;

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public Map<String, UUID> getIDs() {
        return ids;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSessionDefinition that = (UserSessionDefinition) o;
        return limit == that.limit && ids.equals(that.ids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(limit, ids);
    }

    @Override
    public UserSessionDefinition deepClone() {
        UserSessionDefinition definition = new UserSessionDefinition();

        definition.limit = this.limit;
        definition.ids.putAll(this.ids);

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        this.limit = NumberUtil.readExternalLong(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.ids.put(StringUtil.readExternal(in), UUIDUtil.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        NumberUtil.writeExternalLong(out, this.limit);

        NumberUtil.writeExternalInteger(out, this.ids.size());
        for (Map.Entry<String, UUID> pair : this.ids.entrySet()) {
            StringUtil.writeExternal(out, pair.getKey());
            UUIDUtil.writeExternal(out, pair.getValue());
        }
    }
}
