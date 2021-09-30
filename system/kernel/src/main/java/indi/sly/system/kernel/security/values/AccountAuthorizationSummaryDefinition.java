package indi.sly.system.kernel.security.values;

import java.util.UUID;

public class AccountAuthorizationSummaryDefinition {
    public AccountAuthorizationSummaryDefinition() {
        this.token = new AccountAuthorizationTokenDefinition();
    }

    private UUID id;
    private String name;
    private String password;
    private final AccountAuthorizationTokenDefinition token;

    public UUID getID() {
        return this.id;
    }

    public void setID(UUID ID) {
        this.id = ID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountAuthorizationTokenDefinition getToken() {
        return this.token;
    }
}
