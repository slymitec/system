package indi.sly.subsystem.periphery.proxies.values;

import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HandleEntryDefinition extends ADefinition {
    public HandleEntryDefinition() {
        this.date = new HashMap<>();
    }

    private UUID handle;
    private String clazz;
    private final Map<Long, Long> date;

    public UUID getHandle() {
        return this.handle;
    }

    public void setHandle(UUID handle) {
        this.handle = handle;
    }

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public Map<Long, Long> getDate() {
        return this.date;
    }
}
