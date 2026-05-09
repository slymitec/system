package indi.sly.system.kernel.security.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class AccountSessionsDefinition extends ADefinition {
    public AccountSessionsDefinition() {
        this.sessions = new HashSet<>();
    }

    private final Set<UUID> sessions;

    public Set<UUID> getSessions() {
        return this.sessions;
    }
}
