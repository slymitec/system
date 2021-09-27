package indi.sly.system.services.auxiliary.values;

import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;

public class UserContentRequestRawDefinition extends ADefinition<UserContentRequestRawDefinition> {
    public UserContentRequestRawDefinition() {
        this.request = new HashMap<>();
    }

    private String job;
    private String method;
    private final Map<String, String> request;

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
}
