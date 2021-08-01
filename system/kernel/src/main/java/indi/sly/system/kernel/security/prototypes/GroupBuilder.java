package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.UserRepositoryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.values.AccountAuthorizationTokenDefinition;
import indi.sly.system.kernel.security.values.GroupEntity;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GroupBuilder extends APrototype {
    protected UserFactory factory;
    protected ProcessTokenObject processToken;

    public GroupObject create(String groupName) {
        if (StringUtil.isNameIllegal(groupName)) {
            throw new ConditionParametersException();
        }

        if (!processToken.isPrivileges(PrivilegeType.SECURITY_MODIFY_ACCOUNT_AND_GROUP)) {
            throw new ConditionPermissionsException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getUserRepository();

        GroupEntity group = new GroupEntity();
        group.setID(UUID.randomUUID());
        group.setName(groupName);
        AccountAuthorizationTokenDefinition accountGroupToken = new AccountAuthorizationTokenDefinition();
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

    public void delete(UUID groupID) {
        if (ValueUtil.isAnyNullOrEmpty(groupID)) {
            throw new ConditionParametersException();
        }

        if (!processToken.isPrivileges(PrivilegeType.SECURITY_MODIFY_ACCOUNT_AND_GROUP)) {
            throw new ConditionPermissionsException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getUserRepository();

        GroupEntity group = userRepository.getGroup(groupID);
        userRepository.delete(group);
    }
}
