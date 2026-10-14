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
import indi.sly.system.kernel.security.values.*;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GroupTokenObject extends AChildCacheableObject<GroupChildCacheEntity, GroupObject> {
    @SuppressWarnings("NullAway.Init")
    protected UserFactory factory;

    private GroupEntity getSelf(long lock) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        UserRepositoryObject accountGroupRepository = memoryManager.getUserRepository();

        return accountGroupRepository.getGroup(this.cache.getGroup().getGroupId(), lock);
    }

    public final long getPrivileges() {
        UserTokenEntity userToken = this.getSelf(LockType.READ).getToken();
        return userToken.getPrivileges();
    }

    public final void setPrivileges(long privileges) {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivileges(PrivilegeType.CORE_MODIFY_PRIVILEGES)) {
            throw new ConditionRefuseException();
        }

        GroupEntity group = this.getSelf(LockType.WRITE);

        UserTokenEntity userToken = group.getToken();
        userToken.setPrivileges(privileges);

        group.setToken(userToken);
    }

    public final Map<Long, Integer> getLimits() {
        UserTokenEntity userToken = this.getSelf(LockType.READ).getToken();
        return CollectionUtil.unmodifiable(userToken.getLimits());
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

        GroupEntity group = this.getSelf(LockType.WRITE);

        UserTokenEntity userToken = group.getToken();
        userToken.getLimits().clear();
        userToken.getLimits().putAll(limits);

        group.setToken(userToken);
    }
}
