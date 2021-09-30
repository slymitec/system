package indi.sly.system.kernel.security.values;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccountAuthorizationSummaryDefinition {
    public AccountAuthorizationSummaryDefinition() {
        this.token = new AccountAuthorizationTokenDefinition();
        this.sessions = new HashMap<>();
    }

    private UUID id;
    private String name;
    private String password;
    private final AccountAuthorizationTokenDefinition token;
    private final Map<UUID, String> sessions;

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

    public Map<UUID, String> getSessions() {
        return this.sessions;
    }
}
