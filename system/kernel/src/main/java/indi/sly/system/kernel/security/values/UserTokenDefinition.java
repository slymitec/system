package indi.sly.system.kernel.security.values;

import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserTokenDefinition extends ADefinition {
    public UserTokenDefinition() {
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
}
