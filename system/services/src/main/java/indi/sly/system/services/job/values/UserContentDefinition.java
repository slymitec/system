package indi.sly.system.services.job.values;

import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;

public class UserContentDefinition extends ADefinition<UserContentDefinition> {
    public UserContentDefinition() {
        this.request = new HashMap<>();
        this.response = new HashMap<>();
        this.exception = new UserContentExceptionDefinition();
    }

    private String task;
    private String method;
    private final Map<String, String> request;
    private final Map<String, String> response;
    private final UserContentExceptionDefinition exception;

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

    public Map<String, String> getResponse() {
        return this.response;
    }

    public UserContentExceptionDefinition getException() {
        return this.exception;
    }
}
