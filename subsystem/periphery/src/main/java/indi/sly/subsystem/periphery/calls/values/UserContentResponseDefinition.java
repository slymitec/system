package indi.sly.subsystem.periphery.calls.values;

import indi.sly.system.common.values.ADefinition;

public class UserContentResponseDefinition extends ADefinition<UserContentResponseDefinition> {
    public UserContentResponseDefinition() {
        this.result = new UserContentResponseResultDefinition();
        this.exception = new UserContentResponseExceptionDefinition();
    }

    private final UserContentResponseResultDefinition result;
    private final UserContentResponseExceptionDefinition exception;

    public UserContentResponseResultDefinition getResult() {
        return this.result;
    }

    public UserContentResponseExceptionDefinition getException() {
        return this.exception;
    }
}
