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
import indi.sly.system.kernel.security.values.AccountAuthorizationTokenDefinition;
import indi.sly.system.kernel.security.values.AccountEntity;
import indi.sly.system.kernel.security.values.GroupEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
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

                UUID[] groupIDs = new UUID[]{kernelConfiguration.SECURITY_GROUP_ADMINISTRATORS_ID,
                        kernelConfiguration.SECURITY_GROUP_SYSTEMS_ID, kernelConfiguration.SECURITY_GROUP_USERS_ID};
                String[] groupNames = new String[]{"Administrators", "Systems", "Users"};
                for (int i = 0; i < groupIDs.length; i++) {
                    if (!userRepository.containGroup(groupIDs[i])) {
                        GroupEntity group = new GroupEntity();
                        group.setID(groupIDs[i]);
                        group.setName(groupNames[i]);
                        AccountAuthorizationTokenDefinition accountGroupToken = new AccountAuthorizationTokenDefinition();
                        accountGroupToken.getLimits().putAll(kernelConfiguration.PROCESSES_TOKEN_DEFAULT_LIMIT);
                        group.setToken(ObjectUtil.transferToByteArray(accountGroupToken));

                        userRepository.add(group);
                    }
                }

                if (!userRepository.containAccount(kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_ID)) {
                    GroupEntity group = userRepository.getGroup(kernelConfiguration.SECURITY_GROUP_SYSTEMS_ID);

                    AccountEntity account = new AccountEntity();
                    account.setID(kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_ID);
                    account.setName("System");
                    account.setPassword(StringUtil.EMPTY);
                    account.setGroups(new ArrayList<>(List.of(group)));
                    AccountAuthorizationTokenDefinition accountAuthorizationToken = new AccountAuthorizationTokenDefinition();
                    accountAuthorizationToken.getLimits().putAll(kernelConfiguration.PROCESSES_TOKEN_FULL_LIMIT);
                    account.setToken(ObjectUtil.transferToByteArray(accountAuthorizationToken));

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
