package indi.sly.system.services.jobs.values;

public record ClientResponseRecord(UserContentResponseRecord content, ClientResponseExceptionRecord exception) {
    public ClientResponseRecord(UserContentResponseRecord content) {
        this(content, null);
    }

    public ClientResponseRecord(ClientResponseExceptionRecord exception) {
        this(null, exception);
    }
}
