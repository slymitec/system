package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;

public class ClientRequestDefinition extends ADefinition {
    public ClientRequestDefinition() {
        this.processId = new ClientRequestProcessIdDefinition();
        this.content = new UserContentRequestDefinition();
    }

    private final ClientRequestProcessIdDefinition processId;
    private final UserContentRequestDefinition content;

    public ClientRequestProcessIdDefinition getProcessId() {
        return this.processId;
    }

    public UserContentRequestDefinition getContent() {
        return this.content;
    }
}
