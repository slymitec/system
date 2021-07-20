package indi.sly.system.kernel.security.values;

import java.util.HashMap;
import java.util.Map;

public class AccountAuthorizationTokenDefinition {
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
}
