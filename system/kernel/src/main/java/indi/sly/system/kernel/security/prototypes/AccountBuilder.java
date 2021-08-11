package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.prototypes.ABuilder;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.UserRepositoryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.values.AccountAuthorizationTokenDefinition;
import indi.sly.system.kernel.security.values.AccountEntity;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountBuilder extends ABuilder {
    protected UserFactory factory;
    protected ProcessTokenObject processToken;

    public AccountObject create(String accountName, String accountPassword) {
        if (StringUtil.isNameIllegal(accountName)) {
            throw new ConditionParametersException();
        }
        if (ObjectUtil.isAnyNull(accountPassword)) {
            accountPassword = StringUtil.EMPTY;
        }

        if (!processToken.isPrivileges(PrivilegeType.SECURITY_MODIFY_ACCOUNT_AND_GROUP)) {
            throw new ConditionRefuseException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getUserRepository();

        AccountEntity account = new AccountEntity();
        account.setID(UUID.randomUUID());
        account.setName(accountName);
        account.setPassword(accountPassword);
        AccountAuthorizationTokenDefinition accountAuthorizationToken = new AccountAuthorizationTokenDefinition();
        KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();
        accountAuthorizationToken.getLimits().putAll(kernelConfiguration.PROCESSES_TOKEN_DEFAULT_LIMIT);
        account.setToken(ObjectUtil.transferToByteArray(accountAuthorizationToken));

        try {
            userRepository.getAccount(accountName);

            throw new StatusAlreadyExistedException();
        } catch (StatusNotExistedException exception) {
            userRepository.add(account);
        }

        return this.factory.buildAccount(account);
    }

    public void delete(UUID accountID) {
        if (ValueUtil.isAnyNullOrEmpty(accountID)) {
            throw new ConditionParametersException();
        }

        if (!processToken.isPrivileges(PrivilegeType.SECURITY_MODIFY_ACCOUNT_AND_GROUP)) {
            throw new ConditionRefuseException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getUserRepository();

        AccountEntity account = userRepository.getAccount(accountID);
        userRepository.delete(account);
    }
}
