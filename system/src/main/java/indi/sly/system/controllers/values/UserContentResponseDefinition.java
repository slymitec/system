package indi.sly.system.controllers.values;

import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;

public class UserContentResponseDefinition extends ADefinition<UserContentResponseDefinition> {
    public UserContentResponseDefinition() {
        this.results = new HashMap<>();
        this.exception = new UserContentExceptionDefinition();
    }

    private final Map<String, String> results;
    private final UserContentExceptionDefinition exception;

    public Map<String, String> getResults() {
        return this.results;
    }

    public UserContentExceptionDefinition getException() {
        return this.exception;
    }
}
