package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AIndependentValueProcessObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.UserRepositoryObject;
import indi.sly.system.kernel.security.values.GroupEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GroupObject extends AIndependentValueProcessObject<GroupEntity> {
    public UUID getID() {
        this.lock(LockType.READ);
        this.init();

        UUID id = this.value.getID();

        this.lock(LockType.NONE);
        return id;
    }

    public String getName() {
        this.lock(LockType.READ);
        this.init();

        String name = this.value.getName();

        this.lock(LockType.NONE);
        return name;
    }

    public UserTokenObject getToken() {
        this.lock(LockType.READ);
        this.init();

        UserTokenObject accountGroupToken = this.factoryManager.create(UserTokenObject.class);

        accountGroupToken.setSource(() -> this.value.getToken(), (byte[] source) -> this.value.setToken(source));
        accountGroupToken.setLock((lock) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            UserRepositoryObject accountGroupRepository = memoryManager.getUserRepository();

            accountGroupRepository.lock(this.value, lock);
        });

        this.lock(LockType.NONE);
        return accountGroupToken;
    }
}
