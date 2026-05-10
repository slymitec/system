package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;

import java.util.*;

public class UserContentRequestDefinition extends ADefinition {
    public UserContentRequestDefinition() {
        this.parameters = new ArrayList<>();
    }

    private UUID id;
    private String task;
    private String method;
    private final List<String> parameters;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
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

    public List<String> getParameters() {
        return this.parameters;
    }
}
