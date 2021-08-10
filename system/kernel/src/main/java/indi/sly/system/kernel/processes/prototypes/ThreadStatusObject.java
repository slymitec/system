package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AValueProcessPrototype;
import indi.sly.system.kernel.processes.values.ThreadDefinition;
import indi.sly.system.kernel.processes.values.ThreadStatusType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadStatusObject extends AValueProcessPrototype<ThreadDefinition> {
    public long get() {
        this.init();

        return this.value.getStatus();
    }

    public void initialize() {
        if (this.value.getStatus() != ThreadStatusType.NULL) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setStatus(ThreadStatusType.INITIALIZATION);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public void running() {
        if (this.value.getStatus() != ThreadStatusType.INITIALIZATION) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setStatus(ThreadStatusType.RUNNING);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public void die() {
        if (this.value.getStatus() != ThreadStatusType.RUNNING) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setStatus(ThreadStatusType.DIED);

        this.fresh();
        this.lock(LockType.NONE);
    }
}
