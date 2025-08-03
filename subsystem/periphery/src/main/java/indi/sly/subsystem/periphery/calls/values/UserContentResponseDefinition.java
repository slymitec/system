package indi.sly.subsystem.periphery.calls.values;

import indi.sly.system.common.values.ADefinition;

import java.util.UUID;

public class UserContentResponseDefinition extends ADefinition<UserContentResponseDefinition> {
    public UserContentResponseDefinition() {
        this.result = new UserContentResponseResultDefinition();
        this.exception = new UserContentResponseExceptionDefinition();
    }

    private UUID id;
    private final UserContentResponseResultDefinition result;
    private final UserContentResponseExceptionDefinition exception;

    public UUID getID() {
        return this.id;
    }

    public void setID(UUID id) {
        this.id = id;
    }

    public UserContentResponseResultDefinition getResult() {
        return this.result;
    }

    public UserContentResponseExceptionDefinition getException() {
        return this.exception;
    }
}
