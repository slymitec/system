package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.kernel.core.prototypes.AChildDefinitionObject;
import indi.sly.system.kernel.processes.values.ThreadDefinition;
import indi.sly.system.kernel.processes.values.ThreadStatusType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadStatusObject extends AChildDefinitionObject<ThreadDefinition, ThreadObject> {
    public long get() {
        return this.definition.getStatus();
    }

    public void initialize() {
        if (LogicalUtil.allNotEqual(this.definition.getStatus(), ThreadStatusType.NULL)) {
            throw new StatusRelationshipErrorException();
        }

        this.definition.setStatus(ThreadStatusType.INITIALIZATION);
    }

    public void running() {
        if (LogicalUtil.allNotEqual(this.definition.getStatus(), ThreadStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        this.definition.setStatus(ThreadStatusType.RUNNING);
    }

    public void die() {
        if (LogicalUtil.allNotEqual(this.definition.getStatus(), ThreadStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        this.definition.setStatus(ThreadStatusType.DIED);
    }
}
