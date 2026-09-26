package indi.sly.subsystem.periphery.proxies.values;

import indi.sly.system.common.values.ADefinition;
import org.redisson.api.annotation.RObjectField;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RemoteDefinition extends ADefinition {
    public RemoteDefinition() {
        this.date = new HashMap<>();
    }

    private String task;
    private String value;
    private final Map<Long, Long> date;

    public String getTask() {
        return this.task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Map<Long, Long> getDate() {
        return this.date;
    }
}
