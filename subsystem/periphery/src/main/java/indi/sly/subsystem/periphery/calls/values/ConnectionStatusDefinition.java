package indi.sly.subsystem.periphery.calls.values;

import indi.sly.system.common.values.ADefinition;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ConnectionStatusDefinition extends ADefinition<ConnectionStatusDefinition> {
    public ConnectionStatusDefinition() {
        this.locks = new ConcurrentHashMap<>();
        this.conditions = new ConcurrentHashMap<>();
        this.responses = new ConcurrentHashMap<>();
    }

    private long runtime;
    private Object helper;
    private final Map<UUID, Lock> locks;
    private final Map<UUID, Condition> conditions;
    private final Map<UUID, UserContentResponseDefinition> responses;
    private ExecutorService executor;

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

    public Map<UUID, Lock> getLocks() {
        return this.locks;
    }

    public Map<UUID, Condition> getConditions() {
        return this.conditions;
    }

    public Map<UUID, UserContentResponseDefinition> getResponses() {
        return this.responses;
    }

    public ExecutorService getExecutor() {
        return this.executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }
}
