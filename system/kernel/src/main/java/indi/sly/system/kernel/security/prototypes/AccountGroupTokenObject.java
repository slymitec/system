package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.prototypes.ABytesProcessObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.values.AccountGroupTokenDefinition;
import indi.sly.system.kernel.security.types.PrivilegeTypes;

import java.util.Map;

public class AccountGroupTokenObject extends ABytesProcessObject {

    @Override
    protected void read(byte[] source) {
        this.accountGroupToken = ObjectUtils.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtils.transferToByteArray(this.accountGroupToken);
    }

    private AccountGroupTokenDefinition accountGroupToken;

    public long getPrivilegeTypes() {
        this.init();
        return accountGroupToken.getPrivilegeTypes();
    }

    public void setPrivilegeTypes(long privilegeTypes) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.CORE_MODIFY_PRIVILEGES)) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.accountGroupToken.setPrivilegeTypes(privilegeTypes);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public Map<Long, Integer> getLimits() {
        return this.accountGroupToken.getLimits();
    }

    public void setLimits(Map<Long, Integer> limits) {
        if (ObjectUtils.isAnyNull(limits)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_LIMITS)) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        Map<Long, Integer> accountGroupTokenLimits = this.accountGroupToken.getLimits();
        accountGroupTokenLimits.clear();
        accountGroupTokenLimits.putAll(limits);

        this.fresh();
        this.lock(LockTypes.NONE);
    }
}
