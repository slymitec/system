package indi.sly.system.kernel.security;

import indi.sly.system.common.exceptions.*;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.StringUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.types.StartupTypes;
import indi.sly.system.kernel.core.enviroment.KernelConfiguration;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AccountGroupRepositoryObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.values.AccountGroupTokenDefinition;
import indi.sly.system.kernel.security.values.AccountEntity;
import indi.sly.system.kernel.security.values.GroupEntity;
import indi.sly.system.kernel.security.prototypes.*;
import indi.sly.system.kernel.security.types.PrivilegeTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SecurityTokenManager extends AManager {
    private AccountGroupObjectFactoryObject accountGroupObjectFactory;

    @Override
    public void startup(long startupTypes) {
        if (startupTypes == StartupTypes.STEP_INIT) {
        } else if (startupTypes == StartupTypes.STEP_KERNEL) {
            this.accountGroupObjectFactory = this.factoryManager.create(AccountGroupObjectFactoryObject.class);
        }
    }

    @Override
    public void shutdown() {
    }

    public AccountAuthorizationObject authorize(String accountName) {
        return this.authorize(accountName, null);
    }

    public AccountAuthorizationObject authorize(String accountName, String accountPassword) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        AccountObject account = this.getAccount(accountName);

        AccountAuthorizationObject accountAuthorization = this.factoryManager.create(AccountAuthorizationObject.class);

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.SECURITY_DO_WITH_ANY_ACCOUNT)
                && !StringUtils.equals(account.getPassword(), accountPassword)) {
            throw new ConditionRefuseException();
        }

        accountAuthorization.setSource(account);

        return accountAuthorization;
    }

    public AccountObject getAccount(UUID accountID) {
        if (UUIDUtils.isAnyNullOrEmpty(accountID)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AccountGroupRepositoryObject accountGroupRepository = memoryManager.getAccountGroupRepository();

        AccountEntity account = accountGroupRepository.getAccount(accountID);

        return this.accountGroupObjectFactory.buildAccount(account);
    }

    public AccountObject getAccount(String accountName) {
        if (StringUtils.isNameIllegal(accountName)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AccountGroupRepositoryObject accountGroupRepository = memoryManager.getAccountGroupRepository();

        AccountEntity account = accountGroupRepository.getAccount(accountName);

        return this.accountGroupObjectFactory.buildAccount(account);
    }

    public GroupObject getGroup(UUID groupID) {
        if (UUIDUtils.isAnyNullOrEmpty(groupID)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AccountGroupRepositoryObject accountGroupRepository = memoryManager.getAccountGroupRepository();

        GroupEntity group = accountGroupRepository.getGroup(groupID);

        return this.accountGroupObjectFactory.buildGroup(group);
    }

    public GroupObject getGroup(String groupName) {
        if (StringUtils.isNameIllegal(groupName)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AccountGroupRepositoryObject accountGroupRepository = memoryManager.getAccountGroupRepository();

        GroupEntity group = accountGroupRepository.getGroup(groupName);

        return this.accountGroupObjectFactory.buildGroup(group);
    }

    public AccountObject createAccount(String accountName, String accountPassword) {
        if (StringUtils.isNameIllegal(accountName)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.SECURITY_MODIFY_ACCOUNT_AND_GROUP)) {
            throw new ConditionPermissionsException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AccountGroupRepositoryObject accountGroupRepository = memoryManager.getAccountGroupRepository();

        AccountEntity account = new AccountEntity();
        account.setID(UUID.randomUUID());
        account.setName(accountName);
        account.setPassword(accountPassword);
        AccountGroupTokenDefinition accountGroupToken = new AccountGroupTokenDefinition();
        KernelConfiguration kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();
        accountGroupToken.getLimits().putAll(kernelConfiguration.PROCESSES_TOKEN_DEFAULT_LIMIT);
        account.setToken(ObjectUtils.transferToByteArray(accountGroupToken));

        try {
            accountGroupRepository.getAccount(accountName);

            throw new StatusAlreadyExistedException();
        } catch (StatusNotExistedException exception) {
            accountGroupRepository.add(account);
        }

        return this.accountGroupObjectFactory.buildAccount(account);
    }

    public GroupObject createGroup(String groupName) {
        if (StringUtils.isNameIllegal(groupName)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.SECURITY_MODIFY_ACCOUNT_AND_GROUP)) {
            throw new ConditionPermissionsException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AccountGroupRepositoryObject accountGroupRepository = memoryManager.getAccountGroupRepository();

        GroupEntity group = new GroupEntity();
        group.setID(UUID.randomUUID());
        group.setName(groupName);
        AccountGroupTokenDefinition accountGroupToken = new AccountGroupTokenDefinition();
        KernelConfiguration kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();
        accountGroupToken.getLimits().putAll(kernelConfiguration.PROCESSES_TOKEN_DEFAULT_LIMIT);
        group.setToken(ObjectUtils.transferToByteArray(accountGroupToken));

        try {
            accountGroupRepository.getGroup(groupName);

            throw new StatusAlreadyExistedException();
        } catch (StatusNotExistedException exception) {
            accountGroupRepository.add(group);
        }

        return this.accountGroupObjectFactory.buildGroup(group);
    }

    public void deleteAccount(UUID accountID) {
        if (UUIDUtils.isAnyNullOrEmpty(accountID)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.SECURITY_MODIFY_ACCOUNT_AND_GROUP)) {
            throw new ConditionPermissionsException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AccountGroupRepositoryObject accountGroupRepository = memoryManager.getAccountGroupRepository();

        AccountEntity account = accountGroupRepository.getAccount(accountID);
        accountGroupRepository.delete(account);
    }

    public void deleteGroup(UUID groupID) {
        if (UUIDUtils.isAnyNullOrEmpty(groupID)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.SECURITY_MODIFY_ACCOUNT_AND_GROUP)) {
            throw new ConditionPermissionsException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AccountGroupRepositoryObject accountGroupRepository = memoryManager.getAccountGroupRepository();

        GroupEntity group = accountGroupRepository.getGroup(groupID);
        accountGroupRepository.delete(group);
    }
}
