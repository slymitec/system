package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;

import java.util.UUID;

public class UserContextDefinition extends ADefinition {
    public UserContextDefinition() {
        this.content = new UserContentDefinition();
    }

    private UUID threadId;
    private final UserContentDefinition content;
    private ClientResponseExceptionDefinition exception;

    public UUID getThreadId() {
        return this.threadId;
    }

    public void setThreadId(UUID threadId) {
        this.threadId = threadId;
    }

    public UserContentDefinition getContent() {
        return this.content;
    }

    public ClientResponseExceptionDefinition getException() {
        return this.exception;
    }

    public void setException(ClientResponseExceptionDefinition exception) {
        this.exception = exception;
    }
}
