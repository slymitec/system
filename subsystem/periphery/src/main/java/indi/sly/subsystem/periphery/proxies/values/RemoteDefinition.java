package indi.sly.subsystem.periphery.proxies.values;

import indi.sly.system.common.values.ADefinition;
import org.redisson.api.annotation.RObjectField;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RemoteDefinition extends ADefinition {
    public RemoteDefinition() {
        this.alive = true;
        this.date = new HashMap<>();
    }

    public boolean alive;
    private long type;
    private String clazz;
    private String value;
    private final Map<Long, Long> date;

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public long getType() {
        return this.type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
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
