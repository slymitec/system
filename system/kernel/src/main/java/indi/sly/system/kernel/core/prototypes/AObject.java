package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.MethodScopeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.values.AObjectStatusDefinition;
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
    protected AObjectStatusDefinition<?> status;

    public final UUID getHandle() {
        if (ObjectUtil.isAnyNull(this.status)) {
            throw new StatusNotSupportedException();
        }

        return this.status.getHandle();
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public final UUID cache(long space, UUID handle) {
        if (ObjectUtil.isAnyNull(this.status)) {
            throw new StatusNotSupportedException();
        }

        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new ConditionParametersException();
        }
        if (!ValueUtil.isAnyNullOrEmpty(this.status.getHandle())) {
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

        this.status.setHandle(handle);

        return handle;
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public final UUID cache(long space) {
        return this.cache(space, UUIDUtil.createRandom());
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public final void uncache(long space) {
        if (ObjectUtil.isAnyNull(this.status)) {
            throw new StatusNotSupportedException();
        }

        if (!ValueUtil.isAnyNullOrEmpty(this.status.getHandle())) {
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

        coreObjectRepository.deleteByHandle(space, this.status.getHandle());

        this.status.setHandle(null);
    }
}
