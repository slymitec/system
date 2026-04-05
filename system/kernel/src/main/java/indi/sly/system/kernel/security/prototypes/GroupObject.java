package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AIndependentValueProcessObject;
import indi.sly.system.kernel.security.values.GroupEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GroupObject extends AIndependentValueProcessObject<GroupEntity> {
    public UUID getID() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getID();
        } finally {
            this.unlock(LockType.READ);
        }
    }

    public String getName() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getName();
        } finally {
            this.unlock(LockType.READ);
        }
    }

    public GroupTokenObject getToken() {
        try {
            this.lock(LockType.READ);
            this.init();

            GroupTokenObject groupToken = this.factoryManager.create(GroupTokenObject.class);

            groupToken.setParent(this);
            groupToken.setSource(this.value::getToken, this.value::setToken);

            return groupToken;
        } finally {
            this.unlock(LockType.READ);
        }
    }
}
