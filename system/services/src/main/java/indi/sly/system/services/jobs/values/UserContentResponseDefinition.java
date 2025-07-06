package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;

public class UserContentResponseDefinition extends ADefinition<UserContentResponseDefinition> {
    public UserContentResponseDefinition() {
        this.result = new UserContentResponseResultDefinition();
        this.exception = new UserContentExceptionDefinition();
    }

    private final UserContentResponseResultDefinition result;
    private final UserContentExceptionDefinition exception;

    public UserContentResponseResultDefinition getResult() {
        return this.result;
    }

    public UserContentExceptionDefinition getException() {
        return this.exception;
    }
}
