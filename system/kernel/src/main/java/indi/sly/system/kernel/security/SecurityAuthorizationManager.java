package indi.sly.system.kernel.security;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.prototypes.AccountObject;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SecurityAuthorizationManager extends AManager {
    @Override
    public void startup(long startupTypes) {
        if (startupTypes == StartupType.STEP_INIT) {
        } else if (startupTypes == StartupType.STEP_KERNEL) {
        }
    }

    @Override
    public void shutdown() {
    }

    public AccountAuthorizationObject authorize(String accountName) {
        return this.authorize(accountName, StringUtil.EMPTY);
    }

    public AccountAuthorizationObject authorize(String accountName, String accountPassword) {
        if (StringUtil.isNameIllegal(accountName)) {
            throw new ConditionParametersException();
        }
        if (ObjectUtil.isAnyNull(accountPassword)) {
            accountPassword = StringUtil.EMPTY;
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        UserManager userManager = this.factoryManager.getManager(UserManager.class);
        AccountObject account = userManager.getAccount(accountName);

        AccountAuthorizationObject accountAuthorization = this.factoryManager.create(AccountAuthorizationObject.class);

        if (!processToken.isPrivileges(PrivilegeType.SECURITY_DO_WITH_ANY_ACCOUNT)
                && !ObjectUtil.equals(account.getPassword(), accountPassword)) {
            throw new ConditionRefuseException();
        }

        accountAuthorization.setSource(account);

        return accountAuthorization;
    }
}
