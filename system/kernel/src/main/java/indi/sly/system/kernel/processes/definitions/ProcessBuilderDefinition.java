package indi.sly.system.kernel.processes.definitions;

import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;

import java.util.UUID;

public class ProcessBuilderDefinition {
    private UUID fileHandle;
    private AccountAuthorizationObject accountAuthorization;
    private UUID sessionID;

    public UUID getFileHandle() {
        return this.fileHandle;
    }

    public void setFileHandle(UUID fileHandle) {
        this.fileHandle = fileHandle;
    }

    public AccountAuthorizationObject getAccountAuthorization() {
        return this.accountAuthorization;
    }

    public void setAccountAuthorization(AccountAuthorizationObject accountAuthorization) {
        this.accountAuthorization = accountAuthorization;
    }
}
