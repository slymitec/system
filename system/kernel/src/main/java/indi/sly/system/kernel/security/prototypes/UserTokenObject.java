package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessPrototype;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.values.AccountGroupTokenDefinition;
import indi.sly.system.kernel.security.values.PrivilegeTypes;

import java.util.Map;

public class UserTokenObject extends ABytesValueProcessPrototype<AccountGroupTokenDefinition> {
    public long getPrivileges() {
        this.init();

        return this.value.getPrivileges();
    }

    public void setPrivileges(long privileges) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivileges(PrivilegeTypes.CORE_MODIFY_PRIVILEGES)) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setPrivileges(privileges);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public Map<Long, Integer> getLimits() {
        return this.value.getLimits();
    }

    public void setLimits(Map<Long, Integer> limits) {
        if (ObjectUtil.isAnyNull(limits)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivileges(PrivilegeTypes.PROCESSES_MODIFY_LIMITS)) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        Map<Long, Integer> accountGroupTokenLimits = this.value.getLimits();
        accountGroupTokenLimits.clear();
        accountGroupTokenLimits.putAll(limits);

        this.fresh();
        this.lock(LockType.NONE);
    }
}
