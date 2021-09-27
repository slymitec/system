package indi.sly.system.services.auxiliary.values;

import indi.sly.system.common.values.ADefinition;

import java.util.UUID;

public class UserContextDefinition extends ADefinition<UserContextDefinition> {
    public UserContextDefinition() {
        this.content = new UserContentDefinition();
    }

    private UUID threadID;
    private final UserContentDefinition content;

    public UUID getThreadID() {
        return this.threadID;
    }

    public void setThreadID(UUID threadID) {
        this.threadID = threadID;
    }

    public UserContentDefinition getContent() {
        return this.content;
    }
}
