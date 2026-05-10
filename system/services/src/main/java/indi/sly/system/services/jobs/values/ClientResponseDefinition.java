package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;

public class ClientResponseDefinition extends ADefinition {
    private UserContentResponseDefinition content;
    private ClientResponseExceptionDefinition exception;

    public UserContentResponseDefinition getContent() {
        return content;
    }

    public void setContent(UserContentResponseDefinition content) {
        this.content = content;
    }

    public ClientResponseExceptionDefinition getException() {
        return exception;
    }

    public void setException(ClientResponseExceptionDefinition exception) {
        this.exception = exception;
    }
}
