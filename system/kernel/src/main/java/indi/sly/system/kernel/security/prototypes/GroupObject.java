package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AIndependentValueProcessObject;
import indi.sly.system.kernel.security.values.GroupEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
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
            this.lock(LockType.NONE);
        }
    }

    public String getName() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getName();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public UserTokenObject getToken() {
        try {
            this.lock(LockType.READ);
            this.init();

            UserTokenObject accountGroupToken = this.factoryManager.create(UserTokenObject.class);

            accountGroupToken.setParent(this);
            accountGroupToken.setSource(() -> this.value.getToken(), (byte[] source) -> this.value.setToken(source));

            return accountGroupToken;
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
