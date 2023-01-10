package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AValueProcessObject;
import indi.sly.system.kernel.processes.values.ThreadDefinition;
import indi.sly.system.kernel.processes.values.ThreadStatusType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadStatusObject extends AValueProcessObject<ThreadDefinition, ThreadObject> {
    public long get() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getStatus();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void initialize() {
        try {
            this.lock(LockType.WRITE);
            this.init();

            if (LogicalUtil.allNotEqual(this.value.getStatus(), ThreadStatusType.NULL)) {
                throw new StatusRelationshipErrorException();
            }

            this.value.setStatus(ThreadStatusType.INITIALIZATION);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void running() {
        try {
            this.lock(LockType.WRITE);
            this.init();

            if (LogicalUtil.allNotEqual(this.value.getStatus(), ThreadStatusType.INITIALIZATION)) {
                throw new StatusRelationshipErrorException();
            }

            this.value.setStatus(ThreadStatusType.RUNNING);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void die() {
        try {
            this.lock(LockType.WRITE);
            this.init();

            if (LogicalUtil.allNotEqual(this.value.getStatus(), ThreadStatusType.RUNNING)) {
                throw new StatusRelationshipErrorException();
            }

            this.value.setStatus(ThreadStatusType.DIED);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
