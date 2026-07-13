package indi.sly.system.kernel.core.boot.prototypes.processors;

import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.boot.lang.BootStartConsumer;
import indi.sly.system.kernel.core.boot.prototypes.mediators.BootProcessorMediator;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.environment.containers.KernelConfiguration;
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
            KernelConfiguration kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

            MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

            if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_KERNEL)) {
                UserRepositoryObject userRepository = memoryManager.getUserRepository();

                UUID groupId = kernelConfiguration.SECURITY_GROUP_SYSTEMS_ID;
                String groupName = "Systems";
                if (!userRepository.containGroup(groupId)) {
                    GroupEntity group = new GroupEntity();
                    group.setId(groupId);
                    group.setName(groupName);
                    UserTokenEntity userToken = new UserTokenEntity();
                    userToken.setPrivileges(PrivilegeType.FULL);
                    userToken.getLimits().putAll(kernelConfiguration.PROCESSES_TOKEN_FULL_LIMIT);
                    group.setToken(userToken);

                    userRepository.add(group);
                }
                groupId = kernelConfiguration.SECURITY_GROUP_ADMINISTRATORS_ID;
                groupName = "Administrators";
                if (!userRepository.containGroup(groupId)) {
                    GroupEntity group = new GroupEntity();
                    group.setId(groupId);
                    group.setName(groupName);
                    UserTokenEntity userToken = new UserTokenEntity();
                    userToken.setPrivileges(LogicalUtil.or(PrivilegeType.CORE_MODIFY_DATETIME,
                            PrivilegeType.FILE_SYSTEM_ACCESS_MODIFY_MAPPING, PrivilegeType.SERVICE_MODIFY,
                            PrivilegeType.SECURITY_DO_WITH_ANY_ACCOUNT, PrivilegeType.SECURITY_MODIFY_ACCOUNT_AND_GROUP));
                    userToken.getLimits().putAll(kernelConfiguration.PROCESSES_TOKEN_FULL_LIMIT);
                    group.setToken(userToken);

                    userRepository.add(group);
                }
                groupId = kernelConfiguration.SECURITY_GROUP_USERS_ID;
                groupName = "Users";
                if (!userRepository.containGroup(groupId)) {
                    GroupEntity group = new GroupEntity();
                    group.setId(groupId);
                    group.setName(groupName);
                    UserTokenEntity userToken = new UserTokenEntity();
                    userToken.getLimits().putAll(kernelConfiguration.PROCESSES_TOKEN_DEFAULT_LIMIT);
                    group.setToken(userToken);

                    userRepository.add(group);
                }

                if (!userRepository.containAccount(kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_ID)) {
                    GroupEntity group = userRepository.getGroup(kernelConfiguration.SECURITY_GROUP_SYSTEMS_ID);

                    AccountEntity account = new AccountEntity();
                    account.setId(kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_ID);
                    account.setName("System");
                    account.setPassword(StringUtil.EMPTY);
                    account.setGroups(new ArrayList<>(List.of(group)));
                    account.setToken(new UserTokenEntity());
                    account.setSessions(new AccountSessionsEntity());

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
