package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.kernel.core.prototypes.ACorePrototype;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AccountGroupRepositoryObject;
import indi.sly.system.kernel.security.values.AccountEntity;
import indi.sly.system.kernel.security.values.GroupEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountGroupObjectFactoryObject extends ACorePrototype {
    public AccountObject buildAccount(AccountEntity account) {
        AccountObject accountObject = this.factoryManager.create(AccountObject.class);

        accountObject.setSource(() -> account, (AccountEntity source) -> {
        });
        accountObject.setLock((lockType) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            AccountGroupRepositoryObject accountGroupRepository = memoryManager.getAccountGroupRepository();

            accountGroupRepository.lock(account, lockType);
        });

        return accountObject;
    }

    public GroupObject buildGroup(GroupEntity group) {
        GroupObject groupObject = this.factoryManager.create(GroupObject.class);

        groupObject.setSource(() -> group, (GroupEntity source) -> {
        });
        groupObject.setLock((lockType) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            AccountGroupRepositoryObject accountGroupRepository = memoryManager.getAccountGroupRepository();

            accountGroupRepository.lock(group, lockType);
        });

        return groupObject;
    }
}
