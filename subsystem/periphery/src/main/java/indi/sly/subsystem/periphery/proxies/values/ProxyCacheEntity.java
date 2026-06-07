package indi.sly.subsystem.periphery.proxies.values;

import indi.sly.subsystem.periphery.core.values.ACacheEntity;
import org.redisson.api.annotation.RObjectField;

import java.util.UUID;

public class ProxyCacheEntity extends ACacheEntity {
    private UUID handle;
    private String task;
    @RObjectField
    private ProxyContextCacheEntity context;

    public UUID getHandle() {
        return this.handle;
    }

    public void setHandle(UUID handle) {
        this.handle = handle;
    }

    public String getTask() {
        return this.task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public ProxyContextCacheEntity getContext() {
        return this.context;
    }

    public void setContext(ProxyContextCacheEntity context) {
        this.context = context;
    }
}
