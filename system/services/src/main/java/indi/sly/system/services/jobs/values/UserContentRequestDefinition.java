package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserContentRequestDefinition extends ADefinition<UserContentRequestDefinition> {
    public UserContentRequestDefinition() {
        this.request = new HashMap<>();
    }

    private UUID id;
    private String task;
    private String method;
    private final Map<String, String> request;

    public UUID getID() {
        return this.id;
    }

    public void setID(UUID id) {
        this.id = id;
    }

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
