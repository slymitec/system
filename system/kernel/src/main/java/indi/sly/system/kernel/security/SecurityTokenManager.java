package indi.sly.system.kernel.security;

import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SecurityTokenManager extends AManager {
    @Override
    public void startup(long startupTypes) {
    }

    @Override
    public void shutdown() {
    }

    public void checkPrivilegeTypesInCurrentProcessToken(long privilegeTypes) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();

        ProcessTokenObject processToken = process.getToken();

        if (!processToken.checkPrivilegeTypes(privilegeTypes)) {
            throw new ConditionPermissionsException();
        }
    }
}
