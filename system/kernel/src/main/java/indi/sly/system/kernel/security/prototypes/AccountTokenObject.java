package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AChildCacheableObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.UserRepositoryObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;

import indi.sly.system.kernel.security.values.AccountChildCacheEntity;
import indi.sly.system.kernel.security.values.AccountEntity;
import indi.sly.system.kernel.security.values.PrivilegeType;
import indi.sly.system.kernel.security.values.UserTokenEntity;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountTokenObject extends AChildCacheableObject<AccountChildCacheEntity, AccountObject> {
    protected UserFactory factory;

    private AccountEntity getSelf() {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        UserRepositoryObject accountGroupRepository = memoryManager.getUserRepository();

        return accountGroupRepository.getAccount(this.cache.getAccount().getAccountId());
    }

    public final long getPrivileges() {
        try {
            this.factory.lockAccount(this.cache.getAccount(), LockType.READ);

            UserTokenEntity userToken = this.getSelf().getToken();
            return userToken.getPrivileges();
        } finally {
            this.factory.unlockAccount(this.cache.getAccount(), LockType.READ);
        }
    }

    public final void setPrivileges(long privileges) {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivileges(PrivilegeType.CORE_MODIFY_PRIVILEGES)) {
            throw new ConditionRefuseException();
        }

        try {
            this.factory.lockAccount(this.cache.getAccount(), LockType.WRITE);

            UserTokenEntity userToken = this.getSelf().getToken();
            userToken.setPrivileges(privileges);

            this.getSelf().setToken(userToken);
        } finally {
            this.factory.unlockAccount(this.cache.getAccount(), LockType.WRITE);
        }
    }

    public final Map<Long, Integer> getLimits() {
        try {
            this.factory.lockAccount(this.cache.getAccount(), LockType.READ);

            UserTokenEntity userToken = this.getSelf().getToken();
            return CollectionUtil.unmodifiable(userToken.getLimits());
        } finally {
            this.factory.unlockAccount(this.cache.getAccount(), LockType.READ);
        }
    }

    public final void setLimits(Map<Long, Integer> limits) {
        if (ObjectUtil.isAnyNull(limits)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_LIMITS)) {
            throw new ConditionRefuseException();
        }

        try {
            this.factory.lockAccount(this.cache.getAccount(), LockType.WRITE);

            UserTokenEntity userToken = this.getSelf().getToken();
            userToken.getLimits().clear();
            userToken.getLimits().putAll(limits);

            this.getSelf().setToken(userToken);
        } finally {
            this.factory.unlockAccount(this.cache.getAccount(), LockType.WRITE);
        }
    }
}
