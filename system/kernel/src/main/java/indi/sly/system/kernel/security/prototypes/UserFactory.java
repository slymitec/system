package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.Consumer1;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.UserRepositoryObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.security.values.AccountEntity;
import indi.sly.system.kernel.security.values.GroupEntity;
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

    private AccountObject buildAccount(Provider<AccountEntity> funcRead, Consumer1<AccountEntity> funcWrite,
                                       Consumer1<Long> funcLock, Consumer1<Long> funcUnLock) {
        AccountObject account = this.factoryManager.create(AccountObject.class);

        account.setSource(funcRead, funcWrite);
        account.setLock(funcLock, funcUnLock);

        return account;
    }

    public AccountObject buildAccount(UUID accountID) {
        if (ValueUtil.isAnyNullOrEmpty(accountID)) {
            throw new ConditionParametersException();
        }

        Provider<AccountEntity> funcRead = () -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            UserRepositoryObject accountGroupRepository = memoryManager.getUserRepository();

            return accountGroupRepository.getAccount(accountID);
        };
        Consumer1<AccountEntity> funcWrite = (AccountEntity source) -> {
        };
        Consumer1<Long> funcLock = (lock) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            UserRepositoryObject accountGroupRepository = memoryManager.getUserRepository();

            accountGroupRepository.lock(accountGroupRepository.getAccount(accountID), lock);
        };
        Consumer1<Long> funcUnlock = (lock) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            UserRepositoryObject accountGroupRepository = memoryManager.getUserRepository();

            accountGroupRepository.unlock(accountGroupRepository.getAccount(accountID), lock);
        };

        return this.buildAccount(funcRead, funcWrite, funcLock, funcUnlock);
    }

    private GroupObject buildGroup(Provider<GroupEntity> funcRead, Consumer1<GroupEntity> funcWrite,
                                   Consumer1<Long> funcLock, Consumer1<Long> funcUnLock) {
        GroupObject group = this.factoryManager.create(GroupObject.class);

        group.setSource(funcRead, funcWrite);
        group.setLock(funcLock, funcUnLock);

        return group;
    }

    public GroupObject buildGroup(UUID groupID) {
        if (ValueUtil.isAnyNullOrEmpty(groupID)) {
            throw new ConditionParametersException();
        }

        Provider<GroupEntity> funcRead = () -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            UserRepositoryObject accountGroupRepository = memoryManager.getUserRepository();

            return accountGroupRepository.getGroup(groupID);
        };
        Consumer1<GroupEntity> funcWrite = (GroupEntity source) -> {
        };
        Consumer1<Long> funcLock = (lock) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            UserRepositoryObject accountGroupRepository = memoryManager.getUserRepository();

            accountGroupRepository.lock(accountGroupRepository.getGroup(groupID), lock);
        };
        Consumer1<Long> funcUnlock = (lock) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            UserRepositoryObject accountGroupRepository = memoryManager.getUserRepository();

            accountGroupRepository.unlock(accountGroupRepository.getGroup(groupID), lock);
        };

        return this.buildGroup(funcRead, funcWrite, funcLock, funcUnlock);
    }

    public AccountBuilder createAccount() {
        AccountBuilder accountBuilder = this.factoryManager.create(AccountBuilder.class);

        accountBuilder.factory = this;
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        accountBuilder.processToken = process.getToken();

        return accountBuilder;
    }

    public GroupBuilder createGroup() {
        GroupBuilder groupBuilder = this.factoryManager.create(GroupBuilder.class);

        groupBuilder.factory = this;
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        groupBuilder.processToken = process.getToken();

        return groupBuilder;
    }
}
