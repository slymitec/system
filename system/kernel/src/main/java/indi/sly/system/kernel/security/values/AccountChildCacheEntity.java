package indi.sly.system.kernel.security.values;

import com.redis.om.spring.annotations.Document;
import indi.sly.system.kernel.core.values.ACacheEntity;
import org.springframework.data.annotation.Reference;

import java.util.Objects;

@Document("AccountChildObject")
public class AccountChildCacheEntity extends ACacheEntity {
    @Reference
    private AccountCacheEntity account;

    public AccountCacheEntity getAccount() {
        return account;
    }

    public void setAccount(AccountCacheEntity account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AccountChildCacheEntity that = (AccountChildCacheEntity) o;
        return Objects.equals(account, that.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), account);
    }
}
