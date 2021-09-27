package indi.sly.system.services.auxiliary.values;

import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;

public class UserContentResponseRawDefinition extends ADefinition<UserContentResponseRawDefinition> {
    public UserContentResponseRawDefinition() {
        this.response = new HashMap<>();
        this.exception = new UserContentExceptionDefinition();
    }

    private final Map<String, String> response;
    private final UserContentExceptionDefinition exception;

    public Map<String, String> getResponse() {
        return this.response;
    }

    public UserContentExceptionDefinition getException() {
        return this.exception;
    }
}
