package indi.sly.system.kernel.core.boot.prototypes.processors;

import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.boot.lang.BootStartConsumer;
import indi.sly.system.kernel.core.boot.prototypes.wrappers.BootProcessorMediator;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.values.*;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BootProcessesResolver extends ABootResolver {
    public BootProcessesResolver() {
        this.start = (startup) -> {
            KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);

            if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_KERNEL)) {
                ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

                if (!processRepository.contain(kernelConfiguration.PROCESSES_PROTOTYPE_SYSTEM_ID)) {
                    ProcessEntity process = new ProcessEntity();

                    process.setID(kernelConfiguration.PROCESSES_PROTOTYPE_SYSTEM_ID);
                    process.setStatus(ProcessStatusType.RUNNING);
                    process.setCommunication(ObjectUtil.transferToByteArray(new ProcessCommunicationDefinition()));
                    ProcessContextDefinition context = new ProcessContextDefinition();
                    context.setType(ProcessContextType.EXECUTABLE);
                    context.setParameters(StringUtil.EMPTY);
                    process.setContext(ObjectUtil.transferToByteArray(context));
                    process.setInfoTable(ObjectUtil.transferToByteArray(new ProcessInfoTableDefinition()));
                    process.setSession(ObjectUtil.transferToByteArray(new ProcessSessionDefinition()));
                    ProcessStatisticsDefinition processStatistics = new ProcessStatisticsDefinition();
                    DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
                    processStatistics.getDate().put(DateTimeType.CREATE, dateTime.getCurrentDateTime());
                    processStatistics.getDate().put(DateTimeType.ACCESS, dateTime.getCurrentDateTime());
                    process.setStatistics(ObjectUtil.transferToByteArray(processStatistics));
                    ProcessTokenDefinition token = new ProcessTokenDefinition();
                    token.setAccountID(kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_ID);
                    token.setPrivileges(PrivilegeType.FULL);
                    token.getLimits().putAll(kernelConfiguration.PROCESSES_TOKEN_FULL_LIMIT);
                    token.getRoles().add(kernelConfiguration.SECURITY_ROLE_EXECUTABLE_ID);
                    process.setToken(ObjectUtil.transferToByteArray(token));

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
