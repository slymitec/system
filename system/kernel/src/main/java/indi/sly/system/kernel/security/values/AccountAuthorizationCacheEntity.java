package indi.sly.system.kernel.security.values;

import indi.sly.system.kernel.processes.values.ProcessChildCacheEntity;
import org.redisson.api.annotation.REntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@REntity
public class AccountAuthorizationCacheEntity extends AccountChildCacheEntity {
    public AccountAuthorizationCacheEntity() {
        this.date = new HashMap<>();
    }

    private String password;
    private final Map<Long, Long> date;
    private ProcessChildCacheEntity processToken;
    private AccountAuthorizationTokenDefinition accountAuthorizationToken;

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<Long, Long> getDate() {
        return this.date;
    }

    public ProcessChildCacheEntity getProcessToken() {
        return processToken;
    }

    public void setProcessToken(ProcessChildCacheEntity processToken) {
        this.processToken = processToken;
    }

    public AccountAuthorizationTokenDefinition getAccountAuthorizationToken() {
        return this.accountAuthorizationToken;
    }

    public void setAccountAuthorizationToken(AccountAuthorizationTokenDefinition accountAuthorizationToken) {
        this.accountAuthorizationToken = accountAuthorizationToken;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AccountAuthorizationCacheEntity that = (AccountAuthorizationCacheEntity) o;
        return Objects.equals(password, that.password) && Objects.equals(date, that.date) && Objects.equals(processToken, that.processToken) && Objects.equals(accountAuthorizationToken, that.accountAuthorizationToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), password, date, processToken, accountAuthorizationToken);
    }
}
