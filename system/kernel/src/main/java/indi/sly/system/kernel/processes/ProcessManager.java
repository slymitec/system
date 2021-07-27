package indi.sly.system.kernel.processes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.processes.prototypes.*;
import indi.sly.system.kernel.processes.values.ProcessCreatorDefinition;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.values.PrivilegeType;
import indi.sly.system.kernel.processes.instances.prototypes.wrappers.SessionTypeInitializer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessManager extends AManager {
    private ProcessFactory factory;

    @Override
    public void startup(long startup) {
        if (startup == StartupType.STEP_INIT) {
        } else if (startup == StartupType.STEP_KERNEL) {
            this.factory = this.factoryManager.create(ProcessFactory.class);
            this.factory.init();

            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);

            KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

            Set<UUID> childTypes = Set.of();

            typeManager.create(kernelConfiguration.PROCESSES_COMMUNICATION_INSTANCE_PORT_ID,
                    kernelConfiguration.PROCESSES_COMMUNICATION_INSTANCE_PORT_NAME,
                    LogicalUtil.or(TypeInitializerAttributeType.CAN_BE_SENT_AND_INHERITED,
                            TypeInitializerAttributeType.CAN_BE_SHARED_READ, TypeInitializerAttributeType.HAS_AUDIT,
                            TypeInitializerAttributeType.HAS_CONTENT, TypeInitializerAttributeType.HAS_PERMISSION,
                            TypeInitializerAttributeType.HAS_PROPERTIES),
                    childTypes, this.factoryManager.create(SessionTypeInitializer.class));

            typeManager.create(kernelConfiguration.PROCESSES_COMMUNICATION_INSTANCE_SIGNAL_ID,
                    kernelConfiguration.PROCESSES_COMMUNICATION_INSTANCE_SIGNAL_NAME,
                    LogicalUtil.or(TypeInitializerAttributeType.CAN_BE_SENT_AND_INHERITED,
                            TypeInitializerAttributeType.CAN_BE_SHARED_READ, TypeInitializerAttributeType.HAS_AUDIT,
                            TypeInitializerAttributeType.HAS_CONTENT, TypeInitializerAttributeType.HAS_PERMISSION,
                            TypeInitializerAttributeType.HAS_PROPERTIES),
                    childTypes, this.factoryManager.create(SessionTypeInitializer.class));
        }
    }

    @Override
    public void shutdown() {
    }

    private ProcessObject getTarget(UUID processID) {
        if (ValueUtil.isAnyNullOrEmpty(processID)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

        return this.factory.buildProcess(processRepository.get(processID));
    }

    public ProcessObject getCurrent() {
        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

        ThreadObject thread = threadManager.getCurrent();

        return this.getTarget(thread.getProcessID());
    }

    public ProcessObject get(UUID processID) {
        return this.get(processID, null);
    }

    public ProcessObject get(UUID processID, AccountAuthorizationObject accountAuthorization) {
        if (ValueUtil.isAnyNullOrEmpty(processID)) {
            throw new ConditionParametersException();
        }

        ProcessObject currentProcess = this.getCurrent();
        if (currentProcess.getID().equals(processID)) {
            return currentProcess;
        }
        ProcessTokenObject currentProcessToken = currentProcess.getToken();

        ProcessObject process = this.getTarget(processID);
        ProcessTokenObject processToken = process.getToken();

        if (!currentProcessToken.getAccountID().equals(processToken.getAccountID())
                && (!currentProcessToken.isPrivileges(PrivilegeType.SECURITY_DO_WITH_ANY_ACCOUNT)
                && !(ObjectUtil.allNotNull(accountAuthorization)
                && accountAuthorization.checkAndGetResult().getAccountID().equals(processToken.getAccountID())))) {
            throw new ConditionPermissionsException();
        }

        return process;
    }

    public ProcessObject create(AccountAuthorizationObject accountAuthorization, Map<String, String> environmentVariable,
                                UUID fileHandle, Map<Long, Integer> limits, Map<String, String> parameters, long privileges,
                                List<IdentificationDefinition> workFolder) {
        ProcessCreatorDefinition processCreator = new ProcessCreatorDefinition();

        if (ObjectUtil.allNotNull(accountAuthorization)) {
            processCreator.setAccountAuthorization(accountAuthorization);
        }
        if (privileges != PrivilegeType.NULL) {
            processCreator.setPrivileges(privileges);
        }
        if (ObjectUtil.allNotNull(limits) && !limits.isEmpty()) {
            processCreator.setLimits(limits);
        }

        processCreator.setFileHandle(fileHandle);

        if (ObjectUtil.allNotNull(environmentVariable) && !environmentVariable.isEmpty()) {
            processCreator.setEnvironmentVariable(environmentVariable);
        }
        if (ObjectUtil.allNotNull(parameters) && !parameters.isEmpty()) {
            processCreator.setParameters(parameters);
        }
        if (ObjectUtil.allNotNull(workFolder)) {
            processCreator.setWorkFolder(workFolder);
        }

        ProcessObject currentProcess = this.getCurrent();
        ProcessCreatorBuilder processCreatorBuilder = this.factory.createProcess(currentProcess, processCreator);

        return processCreatorBuilder.build();
    }
}
