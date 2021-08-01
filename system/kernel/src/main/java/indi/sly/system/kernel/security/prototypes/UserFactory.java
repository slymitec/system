package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.Consumer1;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.UserRepositoryObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.values.AccountEntity;
import indi.sly.system.kernel.security.values.GroupEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserFactory extends AFactory {
    @Override
    public void init() {

    }

    private AccountObject buildAccount(Provider<AccountEntity> funcRead, Consumer1<AccountEntity> funcWrite,
                                       Consumer1<Long> funcLock) {
        AccountObject account = this.factoryManager.create(AccountObject.class);

        account.setSource(funcRead, funcWrite);
        account.setLock(funcLock);

        return account;
    }

    public AccountObject buildAccount(AccountEntity account) {
        if (ObjectUtil.isAnyNull(account)) {
            throw new ConditionParametersException();
        }

        Provider<AccountEntity> funcRead = () -> account;
        Consumer1<AccountEntity> funcWrite = (AccountEntity source) -> {
        };
        Consumer1<Long> funcLock = (lock) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            UserRepositoryObject accountGroupRepository = memoryManager.getUserRepository();

            accountGroupRepository.lock(account, lock);
        };

        return this.buildAccount(funcRead, funcWrite, funcLock);
    }

    private GroupObject buildGroup(Provider<GroupEntity> funcRead, Consumer1<GroupEntity> funcWrite,
                                   Consumer1<Long> funcLock) {
        GroupObject group = this.factoryManager.create(GroupObject.class);

        group.setSource(funcRead, funcWrite);
        group.setLock(funcLock);

        return group;
    }

    public GroupObject buildGroup(GroupEntity group) {
        if (ObjectUtil.isAnyNull(group)) {
            throw new ConditionParametersException();
        }

        Provider<GroupEntity> funcRead = () -> group;
        Consumer1<GroupEntity> funcWrite = (GroupEntity source) -> {
        };
        Consumer1<Long> funcLock = (lock) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            UserRepositoryObject accountGroupRepository = memoryManager.getUserRepository();

            accountGroupRepository.lock(group, lock);
        };

        return this.buildGroup(funcRead, funcWrite, funcLock);
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
