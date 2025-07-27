package indi.sly.subsystem.periphery.core.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyFinishedException;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

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
        if (!ValueUtil.isAnyNullOrEmpty(this.handle)) {
            throw new StatusAlreadyFinishedException();
        }

        CoreObjectRepositoryObject coreObjectRepository = this.factoryManager.getCoreObjectRepository();

        coreObjectRepository.addByHandle(space, handle, this);

        this.handle = handle;

        return this.handle;
    }

    public final UUID cache(long space) {
        return this.cache(space, UUIDUtil.createRandom());
    }

    public final void uncache(long space) {
        if (ValueUtil.isAnyNullOrEmpty(this.handle)) {
            throw new StatusAlreadyFinishedException();
        }

        CoreObjectRepositoryObject coreObjectRepository = this.factoryManager.getCoreObjectRepository();

        coreObjectRepository.deleteByHandle(space, this.handle);

        this.handle = null;
    }
}
