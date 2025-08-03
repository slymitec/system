package indi.sly.subsystem.periphery.calls.values;

import indi.sly.system.common.values.ADefinition;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionStatusDefinition extends ADefinition<ConnectionStatusDefinition> {
    public ConnectionStatusDefinition() {
        this.request = new ConcurrentHashMap<>();
        this.responses = new ConcurrentHashMap<>();
        this.executor = Executors.newSingleThreadExecutor();
    }

    private long runtime;
    private Object helper;
    private final Map<UUID, UserContentRequestDefinition> request;
    private final Map<UUID, UserContentResponseDefinition> responses;
    private final ExecutorService executor;

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

    public Map<UUID, UserContentRequestDefinition> getRequest() {
        return this.request;
    }

    public Map<UUID, UserContentResponseDefinition> getResponses() {
        return this.responses;
    }

    public ExecutorService getExecutor() {
        return this.executor;
    }
}
