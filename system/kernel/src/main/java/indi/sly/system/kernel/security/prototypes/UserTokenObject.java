package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessObject;
import indi.sly.system.kernel.core.prototypes.AIndependentBytesValueProcessObject;
import indi.sly.system.kernel.core.prototypes.AIndependentValueProcessObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.values.AccountEntity;
import indi.sly.system.kernel.security.values.GroupEntity;
import indi.sly.system.kernel.security.values.PrivilegeType;
import indi.sly.system.kernel.security.values.UserTokenDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class UserTokenObject<T extends AIndependentValueProcessObject<?>> extends ABytesValueProcessObject<UserTokenDefinition, T> {
    public final long getPrivileges() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getPrivileges();
        } finally {
            this.unlock(LockType.READ);
        }
    }

    public final void setPrivileges(long privileges) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivileges(PrivilegeType.CORE_MODIFY_PRIVILEGES)) {
            throw new ConditionRefuseException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.setPrivileges(privileges);

            this.fresh();
        } finally {
            this.unlock(LockType.WRITE);
        }
    }

    public final Map<Long, Integer> getLimits() {
        try {
            this.lock(LockType.READ);
            this.init();

            return CollectionUtil.unmodifiable(this.value.getLimits());
        } finally {
            this.unlock(LockType.READ);
        }
    }

    public final void setLimits(Map<Long, Integer> limits) {
        if (ObjectUtil.isAnyNull(limits)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_LIMITS)) {
            throw new ConditionRefuseException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Map<Long, Integer> accountGroupTokenLimits = this.value.getLimits();
            accountGroupTokenLimits.clear();
            accountGroupTokenLimits.putAll(limits);

            this.fresh();
        } finally {
            this.unlock(LockType.WRITE);
        }
    }
}
