package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.ACacheableObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.UserRepositoryObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.values.AccountCacheEntity;
import indi.sly.system.kernel.security.values.AccountEntity;
import indi.sly.system.kernel.security.values.GroupEntity;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountObject extends ACacheableObject<AccountCacheEntity> {
    protected UserFactory factory;

    private AccountEntity getSelf() {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        UserRepositoryObject accountGroupRepository = memoryManager.getUserRepository();

        return accountGroupRepository.getAccount(this.cache.getAccountId());
    }

    public UUID getId() {
        return this.cache.getAccountId();
    }

    public String getName() {
        this.factory.lockAccount(this.cache, LockType.READ);
        try {
            return this.getSelf().getName();
        } finally {
            this.factory.unlockAccount(this.cache, LockType.READ);
        }
    }

    public String getPassword() {
        this.factory.lockAccount(this.cache, LockType.READ);
        try {
            return this.getSelf().getPassword();
        } finally {
            this.factory.unlockAccount(this.cache, LockType.READ);
        }
    }

    public void setPassword(String password) {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.getAccountId().equals(this.getId())
                && !processToken.isPrivileges(PrivilegeType.SECURITY_DO_WITH_ANY_ACCOUNT)) {
            throw new ConditionRefuseException();
        }

        this.factory.lockAccount(this.cache, LockType.WRITE);
        try {
            this.getSelf().setPassword(password);
        } finally {
            this.factory.unlockAccount(this.cache, LockType.WRITE);
        }
    }

    public Set<GroupObject> getGroups() {
        UserManager userManager = this.coreManager.getManager(UserManager.class);

        this.factory.lockAccount(this.cache, LockType.READ);
        try {
            Set<GroupObject> groups = new HashSet<>();

            for (GroupEntity group : this.getSelf().getGroups()) {
                groups.add(userManager.getGroup(group.getId()));
            }

            return CollectionUtil.unmodifiable(groups);
        } finally {
            this.factory.unlockAccount(this.cache, LockType.READ);
        }
    }

    public void setGroups(Set<GroupObject> groups) {
        if (ObjectUtil.isAnyNull(groups)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivileges(PrivilegeType.SECURITY_MODIFY_ACCOUNT_AND_GROUP)) {
            throw new ConditionRefuseException();
        }

        UserRepositoryObject userRepository = memoryManager.getUserRepository();

        this.factory.lockAccount(this.cache, LockType.WRITE);
        try {
            AccountEntity account = this.getSelf();

            if (ObjectUtil.isAnyNull(account.getGroups())) {
                account.setGroups(new ArrayList<>());
            } else {
                account.getGroups().clear();
            }

            for (GroupObject group : groups) {
                account.getGroups().add(userRepository.getGroup(group.getId()));
            }
        } finally {
            this.factory.unlockAccount(this.cache, LockType.WRITE);
        }
    }

    public AccountTokenObject getToken() {
        return this.factory.buildAccountToken(this);
    }

    public AccountSessionsObject getSessions() {
        return this.factory.buildAccountSessions(this);
    }
}
