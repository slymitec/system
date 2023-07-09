package indi.sly.system.services.job.values;

import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;

public class UserContentResponseDefinition extends ADefinition<UserContentResponseDefinition> {
    public UserContentResponseDefinition() {
        this.response = new HashMap<>();
        this.exception = new UserContentExceptionDefinition();
    }

    private final Map<String, Object> response;
    private final UserContentExceptionDefinition exception;

    public Map<String, Object> getResponse() {
        return this.response;
    }

    public UserContentExceptionDefinition getException() {
        return this.exception;
    }
}
