package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyFinishedException;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
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

        CoreObjectRepositoryObject coreObjectRepository = this.factoryManager.getCoreObjectRepository();

        if (!ValueUtil.isAnyNullOrEmpty(this.handle)) {
            throw new StatusAlreadyFinishedException();
        }

        coreObjectRepository.addByHandle(space, handle, this);

        this.handle = handle;

        return this.handle;
    }

    public final UUID cache(long space) {
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
