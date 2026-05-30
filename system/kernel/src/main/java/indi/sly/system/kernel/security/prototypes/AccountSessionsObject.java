package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
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
    protected UserFactory factory;

    private AccountEntity getSelf() {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        UserRepositoryObject accountGroupRepository = memoryManager.getUserRepository();

        return accountGroupRepository.getAccount(this.cache.getAccount().getAccountId());
    }

    public Set<UUID> listSessions() {
        this.factory.lockAccount(this.cache.getAccount(), LockType.READ);
        try {
            AccountSessionsEntity accountSessions = this.getSelf().getSessions();
            return CollectionUtil.unmodifiable(accountSessions.getSessions());
        } finally {
            this.factory.unlockAccount(this.cache.getAccount(), LockType.READ);
        }
    }

    public void addSession(UUID sessionID) {
        if (ValueUtil.isAnyNullOrEmpty(sessionID)) {
            throw new ConditionParametersException();
        }

        this.factory.lockAccount(this.cache.getAccount(), LockType.WRITE);
        try {
            AccountSessionsEntity accountSessions = this.getSelf().getSessions();
            accountSessions.getSessions().add(sessionID);

            this.getSelf().setSessions(accountSessions);
        } finally {
            this.factory.unlockAccount(this.cache.getAccount(), LockType.WRITE);
        }
    }

    public void deleteSession(UUID sessionID) {
        if (ValueUtil.isAnyNullOrEmpty(sessionID)) {
            throw new ConditionParametersException();
        }

        this.factory.lockAccount(this.cache.getAccount(), LockType.WRITE);
        try {
            AccountSessionsEntity accountSessions = this.getSelf().getSessions();
            accountSessions.getSessions().remove(sessionID);

            this.getSelf().setSessions(accountSessions);
        } finally {
            this.factory.unlockAccount(this.cache.getAccount(), LockType.WRITE);
        }
    }
}
