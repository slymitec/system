package indi.sly.system.kernel.core.boot.prototypes.processors;

import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.values.DateTimeType;
import indi.sly.system.kernel.core.boot.lang.BootStartConsumer;
import indi.sly.system.kernel.core.boot.prototypes.mediators.BootProcessorMediator;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.environment.containers.KernelConfiguration;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.values.*;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BootProcessesResolver extends ABootResolver {
    public BootProcessesResolver() {
        this.start = (startup) -> {
            KernelConfiguration kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

            MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

            if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_KERNEL)) {
                ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

                if (!processRepository.contain(kernelConfiguration.PROCESSES_PROTOTYPE_SYSTEM_ID)) {
                    ProcessEntity process = new ProcessEntity();

                    process.setId(kernelConfiguration.PROCESSES_PROTOTYPE_SYSTEM_ID);
                    process.setStatus(ProcessStatusType.RUNNING);
                    process.setCommunication((new ProcessCommunicationEntity()));
                    ProcessContextEntity context = new ProcessContextEntity();
                    context.setType(ProcessContextType.EXECUTABLE);
                    context.setParameters(StringUtil.EMPTY);
                    process.setContext(context);
                    process.setInfoTable(new ProcessInfoTableEntity());
                    process.setSession(new ProcessSessionEntity());
                    ProcessStatisticsEntity processStatistics = new ProcessStatisticsEntity();
                    DateTimeObject dateTime = this.coreManager.getDateTime();
                    processStatistics.getDate().put(DateTimeType.CREATE, dateTime.getCurrent());
                    processStatistics.getDate().put(DateTimeType.ACCESS, dateTime.getCurrent());
                    process.setStatistics(processStatistics);
                    ProcessTokenEntity token = new ProcessTokenEntity();
                    token.setAccountId(kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_ID);
                    token.setPrivileges(PrivilegeType.FULL);
                    token.getLimits().putAll(kernelConfiguration.PROCESSES_TOKEN_FULL_LIMIT);
                    token.getRoles().add(kernelConfiguration.SECURITY_ROLE_EXECUTABLE_ID);
                    process.setToken(token);

                    processRepository.add(process);
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
