package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;

import java.util.UUID;

public class UserContextDefinition extends ADefinition {
    public UserContextDefinition() {
        this.content = new UserContentDefinition();
    }

    private UUID threadId;
    private final UserContentDefinition content;
    private ClientResponseExceptionRecord exception;

    public UUID getThreadId() {
        return this.threadId;
    }

    public void setThreadId(UUID threadId) {
        this.threadId = threadId;
    }

    public UserContentDefinition getContent() {
        return this.content;
    }

    public ClientResponseExceptionRecord getException() {
        return this.exception;
    }

    public void setException(ClientResponseExceptionRecord exception) {
        this.exception = exception;
    }
}
