package indi.sly.subsystem.periphery.calls.values;

import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;

public class ConnectionStatusDefinition extends ADefinition<ConnectionStatusDefinition> {
    public ConnectionStatusDefinition() {
        this.date = new HashMap<>();
    }

    private final Map<Long, Long> date;
    private long runtime;

    public Map<Long, Long> getDate() {
        return this.date;
    }

    public long getRuntime() {
        return this.runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }
}
