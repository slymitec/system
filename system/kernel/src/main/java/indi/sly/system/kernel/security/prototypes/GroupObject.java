package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.ACacheableObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.UserRepositoryObject;
import indi.sly.system.kernel.security.values.GroupCacheEntity;
import indi.sly.system.kernel.security.values.GroupEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GroupObject extends ACacheableObject<GroupCacheEntity> {
    protected UserFactory factory;

    private GroupEntity getSelf() {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        UserRepositoryObject accountGroupRepository = memoryManager.getUserRepository();

        return accountGroupRepository.getGroup(this.cache.getGroupId());
    }

    public UUID getId() {
        return this.cache.getGroupId();
    }

    public String getName() {
        this.factory.lockGroup(this.cache, LockType.READ);
        try {
            return this.getSelf().getName();
        } finally {
            this.factory.unlockGroup(this.cache, LockType.READ);
        }
    }

    public GroupTokenObject getToken() {
        return this.factory.buildGroupToken(this);
    }
}
