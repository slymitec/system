package indi.sly.subsystem.periphery.calls.values;

import indi.sly.system.common.values.ADefinition;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionStatusDefinition extends ADefinition<ConnectionStatusDefinition> {
    public ConnectionStatusDefinition() {
        this.responses = new ConcurrentHashMap<>();
    }

    private long runtime;
    private Object helper;
    private final Map<UUID, UserContentResponseDefinition> responses;

    public long getRuntime() {
        return this.runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    public Object getHelper() {
        return this.helper;
    }

    public void setHelper(Object helper) {
        this.helper = helper;
    }

    public Map<UUID, UserContentResponseDefinition> getResponses() {
        return this.responses;
    }
}
