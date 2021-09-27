package indi.sly.system.services.auxiliary.values;

import indi.sly.system.common.values.ADefinition;

import java.util.UUID;

public class UserContextDefinition extends ADefinition<UserContextDefinition> {
    public UserContextDefinition() {
        this.content = new UserContentDefinition();
    }

    private UUID processID;
    private final UserContentDefinition content;

    public UUID getProcessID() {
        return this.processID;
    }

    public void setProcessID(UUID processID) {
        this.processID = processID;
    }

    public UserContentDefinition getContent() {
        return this.content;
    }
}
