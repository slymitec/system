package indi.sly.system.kernel.security;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.UserRepositoryObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.values.AccountGroupTokenDefinition;
import indi.sly.system.kernel.security.values.AccountEntity;
import indi.sly.system.kernel.security.values.GroupEntity;
import indi.sly.system.kernel.security.prototypes.*;
import indi.sly.system.kernel.security.values.PrivilegeTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserManager extends AManager {
    private UserFactory factory;

    @Override
    public void startup(long startupTypes) {
        if (startupTypes == StartupType.STEP_INIT) {
        } else if (startupTypes == StartupType.STEP_KERNEL) {
            this.factory = this.factoryManager.create(UserFactory.class);
        }
    }

    @Override
    public void shutdown() {
    }

    public AccountObject getAccount(UUID accountID) {
        if (ValueUtil.isAnyNullOrEmpty(accountID)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getAccountGroupRepository();

        AccountEntity account = userRepository.getAccount(accountID);

        return this.factory.buildAccount(account);
    }

    public AccountObject getAccount(String accountName) {
        if (StringUtil.isNameIllegal(accountName)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getAccountGroupRepository();

        AccountEntity account = userRepository.getAccount(accountName);

        return this.factory.buildAccount(account);
    }

    public GroupObject getGroup(UUID groupID) {
        if (ValueUtil.isAnyNullOrEmpty(groupID)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getAccountGroupRepository();

        GroupEntity group = userRepository.getGroup(groupID);

        return this.factory.buildGroup(group);
    }

    public GroupObject getGroup(String groupName) {
        if (StringUtil.isNameIllegal(groupName)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getAccountGroupRepository();

        GroupEntity group = userRepository.getGroup(groupName);

        return this.factory.buildGroup(group);
    }

    public AccountObject createAccount(String accountName, String accountPassword) {
        if (StringUtil.isNameIllegal(accountName)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivilegeType(PrivilegeTypes.SECURITY_MODIFY_ACCOUNT_AND_GROUP)) {
            throw new ConditionPermissionsException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getAccountGroupRepository();

        AccountEntity account = new AccountEntity();
        account.setID(UUID.randomUUID());
        account.setName(accountName);
        account.setPassword(accountPassword);
        AccountGroupTokenDefinition accountGroupToken = new AccountGroupTokenDefinition();
        KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();
        accountGroupToken.getLimits().putAll(kernelConfiguration.PROCESSES_TOKEN_DEFAULT_LIMIT);
        account.setToken(ObjectUtil.transferToByteArray(accountGroupToken));

        try {
            userRepository.getAccount(accountName);

            throw new StatusAlreadyExistedException();
        } catch (StatusNotExistedException exception) {
            userRepository.add(account);
        }

        return this.factory.buildAccount(account);
    }

    public GroupObject createGroup(String groupName) {
        if (StringUtil.isNameIllegal(groupName)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivilegeType(PrivilegeTypes.SECURITY_MODIFY_ACCOUNT_AND_GROUP)) {
            throw new ConditionPermissionsException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getAccountGroupRepository();

        GroupEntity group = new GroupEntity();
        group.setID(UUID.randomUUID());
        group.setName(groupName);
        AccountGroupTokenDefinition accountGroupToken = new AccountGroupTokenDefinition();
        KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();
        accountGroupToken.getLimits().putAll(kernelConfiguration.PROCESSES_TOKEN_DEFAULT_LIMIT);
        group.setToken(ObjectUtil.transferToByteArray(accountGroupToken));

        try {
            userRepository.getGroup(groupName);

            throw new StatusAlreadyExistedException();
        } catch (StatusNotExistedException exception) {
            userRepository.add(group);
        }

        return this.factory.buildGroup(group);
    }

    public void deleteAccount(UUID accountID) {
        if (ValueUtil.isAnyNullOrEmpty(accountID)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivilegeType(PrivilegeTypes.SECURITY_MODIFY_ACCOUNT_AND_GROUP)) {
            throw new ConditionPermissionsException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getAccountGroupRepository();

        AccountEntity account = userRepository.getAccount(accountID);
        userRepository.delete(account);
    }

    public void deleteGroup(UUID groupID) {
        if (ValueUtil.isAnyNullOrEmpty(groupID)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivilegeType(PrivilegeTypes.SECURITY_MODIFY_ACCOUNT_AND_GROUP)) {
            throw new ConditionPermissionsException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getAccountGroupRepository();

        GroupEntity group = userRepository.getGroup(groupID);
        userRepository.delete(group);
    }
}
