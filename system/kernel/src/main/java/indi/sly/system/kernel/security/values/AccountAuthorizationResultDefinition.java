package indi.sly.system.kernel.security.values;

import java.util.UUID;

public class AccountAuthorizationResultDefinition {
    public AccountAuthorizationResultDefinition() {
        this.token = new AccountAuthorizationTokenDefinition();
    }

    private UUID accountID;
    private String accountName;
    private final AccountAuthorizationTokenDefinition token;

    public UUID getAccountID() {
        return this.accountID;
    }

    public void setAccountID(UUID accountID) {
        this.accountID = accountID;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public AccountAuthorizationTokenDefinition getToken() {
        return this.token;
    }
}
