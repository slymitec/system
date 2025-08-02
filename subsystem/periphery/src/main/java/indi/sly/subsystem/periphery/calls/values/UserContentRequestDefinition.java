package indi.sly.subsystem.periphery.calls.values;

import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;

public class UserContentRequestDefinition extends ADefinition<UserContentRequestDefinition> {
    public UserContentRequestDefinition() {
        this.request = new HashMap<>();
    }

    private String task;
    private String method;
    private final Map<String, String> request;

    public String getTask() {
        return this.task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getRequest() {
        return this.request;
    }
}
