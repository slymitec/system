package indi.sly.system.services.job.values;

import indi.sly.system.common.values.ADefinition;

public class UserContextRequestRawDefinition extends ADefinition<UserContextRequestRawDefinition> {
    public UserContextRequestRawDefinition() {
        this.processID = new UserContextRequestProcessIDRawDefinition();
        this.content = new UserContextRequestContentRawDefinition();
    }

    private final UserContextRequestProcessIDRawDefinition processID;
    private final UserContextRequestContentRawDefinition content;

    public UserContextRequestProcessIDRawDefinition getProcessID() {
        return this.processID;
    }

    public UserContextRequestContentRawDefinition getContent() {
        return this.content;
    }
}
