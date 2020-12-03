package indi.sly.system.kernel.security.definitions;

import java.util.UUID;

public class AccountAuthorizationResultDefinition {
    public AccountAuthorizationResultDefinition() {
        this.token = new AccountGroupTokenDefinition();
    }

    private UUID accountID;
    private final AccountGroupTokenDefinition token;

    public UUID getAccountID() {
        return this.accountID;
    }

    public void setAccountID(UUID accountID) {
        this.accountID = accountID;
    }

    public AccountGroupTokenDefinition getToken() {
        return this.token;
    }
}
