package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AccountGroupRepositoryObject;
import indi.sly.system.kernel.security.entities.AccountEntity;
import indi.sly.system.kernel.security.entities.GroupEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountGroupObjectFactoryObject extends ACoreObject {
    public AccountObject buildAccount(AccountEntity account) {
        AccountObject accountObject = this.factoryManager.create(AccountObject.class);

        accountObject.setAccount(account);
        accountObject.setLock((lockType) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            AccountGroupRepositoryObject accountGroupRepository = memoryManager.getAccountGroupRepository();

            accountGroupRepository.lock(account, lockType);
        });

        return accountObject;
    }

    public GroupObject buildGroup(GroupEntity group) {
        GroupObject groupObject = this.factoryManager.create(GroupObject.class);

        groupObject.setGroup(group);
        groupObject.setLock((lockType) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            AccountGroupRepositoryObject accountGroupRepository = memoryManager.getAccountGroupRepository();

            accountGroupRepository.lock(group, lockType);
        });

        return groupObject;
    }
}
