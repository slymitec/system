package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AValueProcessObject;
import indi.sly.system.kernel.processes.values.ThreadDefinition;
import indi.sly.system.kernel.processes.values.ThreadStatusType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadStatusObject extends AValueProcessObject<ThreadDefinition, ThreadObject> {
    public long get() {
        this.lock(LockType.READ);
        this.init();

        long status = this.value.getStatus();

        this.lock(LockType.NONE);
        return status;
    }

    public void initialize() {
        if (LogicalUtil.allNotEqual(this.value.getStatus(), ThreadStatusType.NULL)) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setStatus(ThreadStatusType.INITIALIZATION);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public void running() {
        if (LogicalUtil.allNotEqual(this.value.getStatus(), ThreadStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setStatus(ThreadStatusType.RUNNING);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public void die() {
        if (LogicalUtil.allNotEqual(this.value.getStatus(), ThreadStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setStatus(ThreadStatusType.DIED);

        this.fresh();
        this.lock(LockType.NONE);
    }
}
