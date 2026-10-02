package indi.sly.subsystem.periphery.proxies.values;

import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;

public class RemoteDefinition extends ADefinition {
    public RemoteDefinition() {
        this.date = new HashMap<>();
    }

    private CallContextRecord callContext;
    private String task;
    private String value;
    private final Map<Long, Long> date;

    public CallContextRecord getCallContext() {
        return this.callContext;
    }

    public void setCallContext(CallContextRecord callContext) {
        this.callContext = callContext;
    }

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
