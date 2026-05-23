package indi.sly.system.kernel.security.values;

import indi.sly.system.kernel.core.values.ACacheEntity;
import org.redisson.api.annotation.REntity;

import java.util.Objects;
import java.util.UUID;

@REntity
public class AccountCacheEntity extends ACacheEntity {
    private UUID accountId;

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AccountCacheEntity cache = (AccountCacheEntity) o;
        return Objects.equals(accountId, cache.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), accountId);
    }
}
