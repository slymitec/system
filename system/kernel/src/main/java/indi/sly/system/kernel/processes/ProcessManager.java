package indi.sly.system.kernel.processes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.processes.instances.prototypes.processors.PortTypeInitializer;
import indi.sly.system.kernel.processes.instances.prototypes.processors.SignalTypeInitializer;
import indi.sly.system.kernel.processes.prototypes.*;
import indi.sly.system.kernel.processes.values.ProcessCreatorDefinition;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessManager extends AManager {
    private ProcessFactory factory;

    @Override
    public void startup(long startup) {
        if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_SELF)) {
            this.factory = this.factoryManager.create(ProcessFactory.class);
            this.factory.init();
        } else if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_KERNEL)) {
            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);

            KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

            Set<UUID> childTypes = Set.of();

            typeManager.create(kernelConfiguration.PROCESSES_COMMUNICATION_INSTANCE_PORT_ID,
                    kernelConfiguration.PROCESSES_COMMUNICATION_INSTANCE_PORT_NAME,
                    LogicalUtil.or(TypeInitializerAttributeType.CAN_BE_SENT_AND_INHERITED,
                            TypeInitializerAttributeType.CAN_BE_SHARED_WRITTEN, TypeInitializerAttributeType.HAS_AUDIT,
                            TypeInitializerAttributeType.HAS_CONTENT, TypeInitializerAttributeType.HAS_PERMISSION,
                            TypeInitializerAttributeType.HAS_PROPERTIES),
                    childTypes, this.factoryManager.create(PortTypeInitializer.class));

            typeManager.create(kernelConfiguration.PROCESSES_COMMUNICATION_INSTANCE_SIGNAL_ID,
                    kernelConfiguration.PROCESSES_COMMUNICATION_INSTANCE_SIGNAL_NAME,
                    LogicalUtil.or(TypeInitializerAttributeType.CAN_BE_SENT_AND_INHERITED,
                            TypeInitializerAttributeType.CAN_BE_SHARED_WRITTEN, TypeInitializerAttributeType.HAS_AUDIT,
                            TypeInitializerAttributeType.HAS_CONTENT, TypeInitializerAttributeType.HAS_PERMISSION,
                            TypeInitializerAttributeType.HAS_PROPERTIES),
                    childTypes, this.factoryManager.create(SignalTypeInitializer.class));
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

    public ProcessObject get(UUID processID, AccountAuthorizationObject accountAuthorization) {
        if (ValueUtil.isAnyNullOrEmpty(processID)) {
            throw new ConditionParametersException();
        }

        ProcessObject currentProcess = this.getCurrent();
        if (currentProcess.getID().equals(processID)) {
            return currentProcess;
        }

        ProcessObject process = this.getTarget(processID);

        ProcessSessionObject processSession = process.getSession();
        ProcessTokenObject processToken = process.getToken();
        ProcessSessionObject currentProcessSession = currentProcess.getSession();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();

        if (!currentProcessToken.getAccountID().equals(processToken.getAccountID())
                && (!currentProcessToken.isPrivileges(PrivilegeType.SECURITY_DO_WITH_ANY_ACCOUNT)
                && !(ObjectUtil.allNotNull(accountAuthorization) && accountAuthorization.checkAndGetResult().getID().equals(processToken.getAccountID())))
                && !currentProcessSession.getID().equals(processSession.getID())) {
            throw new ConditionRefuseException();
        }

        return process;
    }

    public ProcessObject get(UUID processID) {
        return this.get(processID, null);
    }

    public ProcessObject create(AccountAuthorizationObject accountAuthorization,
                                Map<String, String> environmentVariables, UUID fileIndex, Map<Long, Integer> limits,
                                String parameters, long privileges, UUID sessionID,
                                List<IdentificationDefinition> workFolder) {
        ProcessCreatorDefinition processCreator = new ProcessCreatorDefinition();

        if (ObjectUtil.allNotNull(accountAuthorization)) {
            processCreator.setAccountAuthorization(accountAuthorization);
        }
        if (LogicalUtil.allNotEqual(privileges, PrivilegeType.NULL)) {
            processCreator.setPrivileges(privileges);
        }
        if (ObjectUtil.allNotNull(limits) && !limits.isEmpty()) {
            processCreator.setLimits(limits);
        }

        processCreator.setFileIndex(fileIndex);

        if (ObjectUtil.allNotNull(environmentVariables) && !environmentVariables.isEmpty()) {
            processCreator.setEnvironmentVariables(environmentVariables);
        }
        if (!ValueUtil.isAnyNullOrEmpty(parameters)) {
            processCreator.setParameters(parameters);
        } else {
            processCreator.setParameters(StringUtil.EMPTY);
        }
        if (!ValueUtil.isAnyNullOrEmpty(sessionID)) {
            processCreator.setSessionID(sessionID);
        }
        if (ObjectUtil.allNotNull(workFolder)) {
            processCreator.setWorkFolder(workFolder);
        }

        ProcessObject currentProcess = this.getCurrent();
        ProcessCreateBuilder processCreateBuilder = this.factory.createProcessCreator(currentProcess);

        return processCreateBuilder.build(processCreator);
    }

    public void endCurrent() {
        ProcessObject process = this.getCurrent();
        ProcessObject parentProcess = null;

        if (ValueUtil.isAnyNullOrEmpty(process.getParentID())) {
            parentProcess = this.getTarget(process.getParentID());
        }

        ProcessEndBuilder processEndBuilder = this.factory.createProcessEnd(parentProcess, process);
        processEndBuilder.build();
    }
}
