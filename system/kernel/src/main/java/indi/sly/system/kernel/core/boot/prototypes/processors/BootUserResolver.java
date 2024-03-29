package indi.sly.system.kernel.core.boot.prototypes.processors;

import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.boot.lang.BootStartConsumer;
import indi.sly.system.kernel.core.boot.prototypes.wrappers.BootProcessorMediator;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.UserRepositoryObject;
import indi.sly.system.kernel.security.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BootUserResolver extends ABootResolver {
    public BootUserResolver() {
        this.start = (startup) -> {
            KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);

            if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_KERNEL)) {
                UserRepositoryObject userRepository = memoryManager.getUserRepository();

                UUID groupID = kernelConfiguration.SECURITY_GROUP_SYSTEMS_ID;
                String groupName = "Systems";
                if (!userRepository.containGroup(groupID)) {
                    GroupEntity group = new GroupEntity();
                    group.setID(groupID);
                    group.setName(groupName);
                    UserTokenDefinition userToken = new UserTokenDefinition();
                    userToken.setPrivileges(PrivilegeType.FULL);
                    userToken.getLimits().putAll(kernelConfiguration.PROCESSES_TOKEN_FULL_LIMIT);
                    group.setToken(ObjectUtil.transferToByteArray(userToken));

                    userRepository.add(group);
                }
                groupID = kernelConfiguration.SECURITY_GROUP_ADMINISTRATORS_ID;
                groupName = "Administrators";
                if (!userRepository.containGroup(groupID)) {
                    GroupEntity group = new GroupEntity();
                    group.setID(groupID);
                    group.setName(groupName);
                    UserTokenDefinition userToken = new UserTokenDefinition();
                    userToken.setPrivileges(LogicalUtil.or(PrivilegeType.CORE_MODIFY_DATETIME,
                            PrivilegeType.FILE_SYSTEM_ACCESS_MODIFY_MAPPING, PrivilegeType.SECURITY_DO_WITH_ANY_ACCOUNT,
                            PrivilegeType.SECURITY_MODIFY_ACCOUNT_AND_GROUP));
                    userToken.getLimits().putAll(kernelConfiguration.PROCESSES_TOKEN_FULL_LIMIT);
                    group.setToken(ObjectUtil.transferToByteArray(userToken));

                    userRepository.add(group);
                }
                groupID = kernelConfiguration.SECURITY_GROUP_USERS_ID;
                groupName = "Users";
                if (!userRepository.containGroup(groupID)) {
                    GroupEntity group = new GroupEntity();
                    group.setID(groupID);
                    group.setName(groupName);
                    UserTokenDefinition userToken = new UserTokenDefinition();
                    userToken.getLimits().putAll(kernelConfiguration.PROCESSES_TOKEN_DEFAULT_LIMIT);
                    group.setToken(ObjectUtil.transferToByteArray(userToken));

                    userRepository.add(group);
                }

                if (!userRepository.containAccount(kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_ID)) {
                    GroupEntity group = userRepository.getGroup(kernelConfiguration.SECURITY_GROUP_SYSTEMS_ID);

                    AccountEntity account = new AccountEntity();
                    account.setID(kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_ID);
                    account.setName("System");
                    account.setPassword(StringUtil.EMPTY);
                    account.setGroups(new ArrayList<>(List.of(group)));
                    account.setToken(ObjectUtil.transferToByteArray(new UserTokenDefinition()));
                    account.setSessions(ObjectUtil.transferToByteArray(new AccountSessionsDefinition()));

                    userRepository.add(account);
                }
            }
        };
    }

    private final BootStartConsumer start;

    @Override
    public void resolve(BootProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
    }

    @Override
    public int order() {
        return 0;
    }
}
