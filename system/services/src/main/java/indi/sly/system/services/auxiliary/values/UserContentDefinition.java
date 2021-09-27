package indi.sly.system.services.auxiliary.values;

import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;

public class UserContentDefinition extends ADefinition<UserContentDefinition> {
    public UserContentDefinition() {
        this.request = new HashMap<>();
        this.response = new HashMap<>();
    }

    private String job;
    private String method;
    private final Map<String, String> request;
    private final Map<String, String> response;

    public String getJob() {
        return this.job;
    }

    public void setJob(String job) {
        this.job = job;
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
}
