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

    public UserTokenObject getToken() {
        try {
            this.lock(LockType.READ);
            this.init();

            UserTokenObject userToken = this.factoryManager.create(UserTokenObject.class);

            userToken.setParent(this);
            userToken.setSource(() -> this.value.getToken(), (byte[] source) -> this.value.setToken(source));

            return userToken;
        } finally {
            this.unlock(LockType.READ);
        }
    }
}
