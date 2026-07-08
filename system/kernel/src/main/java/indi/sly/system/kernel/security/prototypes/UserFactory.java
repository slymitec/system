package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.CacheDurationType;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.CacheRepositoryObject;
import indi.sly.system.kernel.memory.repositories.prototypes.UserRepositoryObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserFactory extends AFactory {
    @Override
    public void init() {
    }

    private AccountObject createAccount(AccountCacheEntity cache) {
        AccountObject account = this.coreManager.create(AccountObject.class);

        account.factory = this;
        account.setCache(cache);

        return account;
    }

    public void lockAccount(AccountCacheEntity cache, long lock) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getUserRepository();

        userRepository.lock(userRepository.getAccount(cache.getAccountId()), lock);
    }

    public void unlockAccount(AccountCacheEntity cache, long lock) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getUserRepository();

        userRepository.unlock(userRepository.getAccount(cache.getAccountId()), lock);
    }

    public AccountObject buildAccount(UUID accountId) {
        if (ValueUtil.isAnyNullOrEmpty(accountId)) {
            throw new ConditionParametersException();
        }

        AccountCacheEntity cache = new AccountCacheEntity();

        cache.setAccountId(accountId);
        cache.setDuration(CacheDurationType.NORMAL);

        return this.createAccount(cache);
    }

    public AccountObject rebuildAccount(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        AccountCacheEntity cache = cacheRepository.get(AccountCacheEntity.class, handle);

        return this.createAccount(cache);
    }

    public AccountObject rebuildAccount(AccountCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        cacheRepository.refresh(AccountCacheEntity.class, cache);

        return this.createAccount(cache);
    }

    private GroupObject createGroup(GroupCacheEntity cache) {
        GroupObject group = this.coreManager.create(GroupObject.class);

        group.factory = this;
        group.setCache(cache);

        return group;
    }

    public void lockGroup(GroupCacheEntity cache, long lock) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getUserRepository();

        userRepository.lock(userRepository.getGroup(cache.getGroupId()), lock);
    }

    public void unlockGroup(GroupCacheEntity cache, long lock) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getUserRepository();

        userRepository.unlock(userRepository.getGroup(cache.getGroupId()), lock);
    }

    public GroupObject buildGroup(UUID groupId) {
        if (ValueUtil.isAnyNullOrEmpty(groupId)) {
            throw new ConditionParametersException();
        }

        GroupCacheEntity cache = new GroupCacheEntity();

        cache.setGroupId(groupId);
        cache.setDuration(CacheDurationType.NORMAL);

        return this.createGroup(cache);
    }

    public GroupObject rebuildGroup(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        GroupCacheEntity cache = cacheRepository.get(GroupCacheEntity.class, handle);

        return this.createGroup(cache);
    }

    public GroupObject rebuildGroup(GroupCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        cacheRepository.refresh(GroupCacheEntity.class, cache);

        return this.createGroup(cache);
    }

    public AccountBuilder createAccount() {
        AccountBuilder accountBuilder = this.coreManager.create(AccountBuilder.class);

        accountBuilder.factory = this;
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        accountBuilder.processToken = process.getToken();

        return accountBuilder;
    }

    public GroupBuilder createGroup() {
        GroupBuilder groupBuilder = this.coreManager.create(GroupBuilder.class);

        groupBuilder.factory = this;
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        groupBuilder.processToken = process.getToken();

        return groupBuilder;
    }

    public AccountTokenObject createAccountToken(AccountObject account, AccountChildCacheEntity cache) {
        AccountTokenObject accountToken = this.coreManager.create(AccountTokenObject.class);

        accountToken.factory = this;
        accountToken.setCache(cache);
        accountToken.setBase(account);

        return accountToken;
    }

    public AccountTokenObject buildAccountToken(AccountObject account) {
        AccountChildCacheEntity cache = new AccountChildCacheEntity();

        cache.setAccount(account.getCache());
        cache.setDuration(CacheDurationType.NORMAL);

        return this.createAccountToken(account, cache);
    }

    public AccountTokenObject rebuildAccountToken(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        AccountChildCacheEntity cache = cacheRepository.get(AccountChildCacheEntity.class, handle);

        return this.rebuildAccountToken(cache);
    }

    public AccountTokenObject rebuildAccountToken(AccountChildCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        cacheRepository.refresh(AccountChildCacheEntity.class, cache);

        AccountObject account = this.rebuildAccount(cache.getAccount());

        return account.getToken();
    }

    public AccountSessionsObject createAccountSessions(AccountObject account, AccountChildCacheEntity cache) {
        AccountSessionsObject accountSessions = this.coreManager.create(AccountSessionsObject.class);

        accountSessions.factory = this;
        accountSessions.setCache(cache);
        accountSessions.setBase(account);

        return accountSessions;
    }

    public AccountSessionsObject buildAccountSessions(AccountObject account) {
        AccountChildCacheEntity cache = new AccountChildCacheEntity();

        cache.setAccount(account.getCache());
        cache.setDuration(CacheDurationType.NORMAL);

        return this.createAccountSessions(account, cache);
    }

    public AccountSessionsObject rebuildAccountSessions(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        AccountChildCacheEntity cache = cacheRepository.get(AccountChildCacheEntity.class, handle);

        return this.rebuildAccountSessions(cache);
    }

    public AccountSessionsObject rebuildAccountSessions(AccountChildCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        cacheRepository.refresh(AccountChildCacheEntity.class, cache);

        AccountObject account = this.rebuildAccount(cache.getAccount());

        return account.getSessions();
    }

    public GroupTokenObject createGroupToken(GroupObject group, GroupChildCacheEntity cache) {
        GroupTokenObject groupToken = this.coreManager.create(GroupTokenObject.class);

        groupToken.factory = this;
        groupToken.setCache(cache);
        groupToken.setBase(group);

        return groupToken;
    }

    public GroupTokenObject buildGroupToken(GroupObject group) {
        GroupChildCacheEntity cache = new GroupChildCacheEntity();

        cache.setDuration(CacheDurationType.NORMAL);

        return this.createGroupToken(group, cache);
    }

    public GroupTokenObject rebuildGroupToken(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        GroupChildCacheEntity cache = cacheRepository.get(GroupChildCacheEntity.class, handle);

        return this.rebuildGroupToken(cache);
    }

    public GroupTokenObject rebuildGroupToken(GroupChildCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        cacheRepository.refresh(GroupChildCacheEntity.class, cache);

        GroupObject group = this.rebuildGroup(cache.getGroup());

        return group.getToken();
    }

    public AccountAuthorizationObject createAccountAuthorization(AccountAuthorizationCacheEntity cache) {
        AccountAuthorizationObject accountAuthorization = this.coreManager.create(AccountAuthorizationObject.class);

        accountAuthorization.factory = this;
        accountAuthorization.setCache(cache);

        return accountAuthorization;
    }

    public AccountAuthorizationObject buildAccountAuthorization(AccountObject account, String password, ProcessTokenObject processToken, AccountAuthorizationTokenRecord accountAuthorizationToken) {
        AccountAuthorizationCacheEntity cache = new AccountAuthorizationCacheEntity();

        cache.setDuration(CacheDurationType.NORMAL);

        cache.setAccount(account.getCache());
        cache.setPassword(password);

        DateTimeObject dateTime = this.coreManager.getDateTime();
        long nowDateTime = dateTime.getCurrent();
        cache.getDate().put(DateTimeType.CREATE, nowDateTime);
        cache.getDate().put(DateTimeType.ACCESS, nowDateTime);

        cache.setProcessToken(processToken.getCache());

        if (ObjectUtil.allNotNull(processToken, accountAuthorizationToken)) {
            cache.setAccountAuthorizationToken(accountAuthorizationToken);
        }

        return this.createAccountAuthorization(cache);
    }

    public AccountAuthorizationObject rebuildAccountAuthorization(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        AccountAuthorizationCacheEntity cache = cacheRepository.get(AccountAuthorizationCacheEntity.class, handle);

        return this.rebuildGAccountAuthorization(cache);
    }

    public AccountAuthorizationObject rebuildGAccountAuthorization(AccountAuthorizationCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        cacheRepository.refresh(AccountAuthorizationCacheEntity.class, cache);

        return this.createAccountAuthorization(cache);
    }
}
