package indi.sly.system.kernel.security.values;

import indi.sly.system.common.values.ADefinition;

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
}
