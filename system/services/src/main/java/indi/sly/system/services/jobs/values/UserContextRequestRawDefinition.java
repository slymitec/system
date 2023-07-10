package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;

public class UserContextRequestRawDefinition extends ADefinition<UserContextRequestRawDefinition> {
    public UserContextRequestRawDefinition() {
        this.processID = new UserContextRequestProcessIDRawDefinition();
        this.content = new UserContentRequestDefinition();
    }

    private final UserContextRequestProcessIDRawDefinition processID;
    private final UserContentRequestDefinition content;

    public UserContextRequestProcessIDRawDefinition getProcessID() {
        return this.processID;
    }

    public UserContentRequestDefinition getContent() {
        return this.content;
    }
}
