package indi.sly.system.kernel.security.values;

import com.redis.om.spring.annotations.Document;
import indi.sly.system.kernel.core.values.ACacheEntity;

import java.util.Objects;
import java.util.UUID;

@Document("AccountObject")
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
