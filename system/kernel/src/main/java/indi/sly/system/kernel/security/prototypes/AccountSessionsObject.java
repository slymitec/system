package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.MethodScope;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.values.MethodScopeType;
import indi.sly.system.kernel.core.prototypes.AIndependentBytesValueProcessObject;
import indi.sly.system.kernel.security.values.AccountSessionsDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountSessionsObject extends AIndependentBytesValueProcessObject<AccountSessionsDefinition> {
    public Set<UUID> listSessions() {
        try {
            this.lock(LockType.READ);
            this.init();

            return CollectionUtil.unmodifiable(this.value.getSessions());
        } finally {
            this.lock(LockType.NONE);
        }
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public void addSession(UUID sessionID) {
        if (ValueUtil.isAnyNullOrEmpty(sessionID)) {
            throw new ConditionParametersException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.getSessions().add(sessionID);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public void deleteSession(UUID sessionID) {
        if (ValueUtil.isAnyNullOrEmpty(sessionID)) {
            throw new ConditionParametersException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.getSessions().remove(sessionID);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
