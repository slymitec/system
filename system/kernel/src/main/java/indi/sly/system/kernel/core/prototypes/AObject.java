package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.lang.MethodScope;
import indi.sly.system.common.lang.StatusAlreadyFinishedException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.MethodScopeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AObject extends APrototype {
    protected UUID handle;

    public final UUID getHandle() {
        return this.handle;
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public final UUID cache(long space, UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new ConditionParametersException();
        }
        if (!ValueUtil.isAnyNullOrEmpty(this.handle)) {
            throw new StatusAlreadyFinishedException();
        }

        if (LogicalUtil.isAnyEqual(space, SpaceType.KERNEL)) {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessTokenObject processToken = process.getToken();

            if (!processToken.isPrivileges(PrivilegeType.CORE_CACHE_OBJECT_IN_KERNEL_SPACE)) {
                throw new ConditionRefuseException();
            }
        }

        CoreObjectRepositoryObject coreObjectRepository = this.factoryManager.getCoreObjectRepository();

        coreObjectRepository.addByHandle(space, handle, this);

        this.handle = handle;

        return this.handle;
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public final UUID cache(long space) {
        return this.cache(space, UUIDUtil.createRandom());
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public final void uncache(long space) {
        if (ValueUtil.isAnyNullOrEmpty(this.handle)) {
            throw new StatusAlreadyFinishedException();
        }

        if (LogicalUtil.isAnyEqual(space, SpaceType.KERNEL)) {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessTokenObject processToken = process.getToken();

            if (!processToken.isPrivileges(PrivilegeType.CORE_CACHE_OBJECT_IN_KERNEL_SPACE)) {
                throw new ConditionRefuseException();
            }
        }

        CoreObjectRepositoryObject coreObjectRepository = this.factoryManager.getCoreObjectRepository();

        coreObjectRepository.deleteByHandle(space, this.handle);

        this.handle = null;
    }
}
