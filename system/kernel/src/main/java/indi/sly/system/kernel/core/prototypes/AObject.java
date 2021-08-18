package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.lang.StatusAlreadyFinishedException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AObject extends APrototype {
    protected UUID handle;

    public final UUID getHandle() {
        return this.handle;
    }

    public final UUID cache(long space, UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new ConditionParametersException();
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

        if (!ValueUtil.isAnyNullOrEmpty(this.handle)) {
            throw new StatusAlreadyFinishedException();
        }

        coreObjectRepository.addByHandle(space, handle, this);

        this.handle = handle;

        return this.handle;
    }

    public final UUID cache(long space) {
        if (LogicalUtil.isAnyEqual(space, SpaceType.KERNEL)) {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessTokenObject processToken = process.getToken();

            if (!processToken.isPrivileges(PrivilegeType.CORE_CACHE_OBJECT_IN_KERNEL_SPACE)) {
                throw new ConditionRefuseException();
            }
        }

        CoreObjectRepositoryObject coreObjectRepository = this.factoryManager.getCoreObjectRepository();

        if (!ValueUtil.isAnyNullOrEmpty(this.handle)) {
            throw new StatusAlreadyFinishedException();
        }

        this.handle = UUIDUtil.createRandom();

        coreObjectRepository.addByHandle(space, this.handle, this);

        return this.handle;
    }

    public final void uncache(long space) {
        CoreObjectRepositoryObject coreObjectRepository = this.factoryManager.getCoreObjectRepository();

        if (ValueUtil.isAnyNullOrEmpty(this.handle)) {
            throw new StatusAlreadyFinishedException();
        }

        coreObjectRepository.deleteByHandle(space, this.handle);

        this.handle = null;
    }
}
