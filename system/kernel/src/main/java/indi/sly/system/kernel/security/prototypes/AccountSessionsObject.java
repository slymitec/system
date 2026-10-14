package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AChildCacheableObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.UserRepositoryObject;
import indi.sly.system.kernel.security.values.AccountChildCacheEntity;
import indi.sly.system.kernel.security.values.AccountEntity;
import indi.sly.system.kernel.security.values.AccountSessionsEntity;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountSessionsObject extends AChildCacheableObject<AccountChildCacheEntity, AccountObject> {
    @SuppressWarnings("NullAway.Init")
    protected UserFactory factory;

    private AccountEntity getSelf(long lock) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        UserRepositoryObject accountGroupRepository = memoryManager.getUserRepository();

        return accountGroupRepository.getAccount(this.cache.getAccount().getAccountId(), lock);
    }

    public Set<UUID> listSessions() {
        AccountSessionsEntity accountSessions = this.getSelf(LockType.READ).getSessions();
        return CollectionUtil.unmodifiable(accountSessions.getSessions());
    }

    public void addSession(UUID sessionId) {
        if (ValueUtil.isAnyNullOrEmpty(sessionId)) {
            throw new ConditionParametersException();
        }

        AccountEntity account = this.getSelf(LockType.WRITE);

        AccountSessionsEntity accountSessions = account.getSessions();
        accountSessions.getSessions().add(sessionId);

        account.setSessions(accountSessions);
    }

    public void deleteSession(UUID sessionId) {
        if (ValueUtil.isAnyNullOrEmpty(sessionId)) {
            throw new ConditionParametersException();
        }

        AccountEntity account = this.getSelf(LockType.WRITE);

        AccountSessionsEntity accountSessions = account.getSessions();
        accountSessions.getSessions().remove(sessionId);

        account.setSessions(accountSessions);
    }
}
