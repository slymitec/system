package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.kernel.core.prototypes.AValueProcessPrototype;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AccountGroupRepositoryObject;
import indi.sly.system.kernel.security.values.GroupEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GroupObject extends AValueProcessPrototype<GroupEntity> {
    public UUID getID() {
        this.init();

        return this.value.getID();
    }

    public String getName() {
        this.init();

        return this.value.getName();
    }

    public AccountGroupTokenObject getToken() {
        this.init();

        AccountGroupTokenObject accountGroupToken = this.factoryManager.create(AccountGroupTokenObject.class);

        accountGroupToken.setSource(() -> this.value.getToken(), (byte[] source) -> this.value.setToken(source));
        accountGroupToken.setLock((lockType) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            AccountGroupRepositoryObject accountGroupRepository = memoryManager.getAccountGroupRepository();

            accountGroupRepository.lock(this.value, lockType);
        });

        return accountGroupToken;
    }
}
