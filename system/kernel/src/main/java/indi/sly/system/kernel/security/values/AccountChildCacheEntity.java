package indi.sly.system.kernel.security.values;

import indi.sly.system.kernel.core.values.ACacheEntity;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RObjectField;

import java.util.Objects;

@REntity
public class AccountChildCacheEntity extends ACacheEntity {
    @RObjectField
    private AccountCacheEntity account;

    public AccountCacheEntity getAccount() {
        return account;
    }

    public void setAccount(AccountCacheEntity account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AccountChildCacheEntity cache)) return false;
        return Objects.equals(id, cache.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
