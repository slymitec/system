package indi.sly.system.kernel.processes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
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
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.processes.instances.prototypes.processors.PortTypeInitializer;
import indi.sly.system.kernel.processes.instances.prototypes.processors.SessionTypeInitializer;
import indi.sly.system.kernel.processes.instances.prototypes.processors.SignalTypeInitializer;
import indi.sly.system.kernel.processes.prototypes.*;
import indi.sly.system.kernel.processes.values.ProcessCreatorDefinition;
import indi.sly.system.kernel.processes.values.ThreadStatusType;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.List;
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

            long attribute = LogicalUtil.or(TypeInitializerAttributeType.CAN_BE_SHARED_WRITTEN,
                    TypeInitializerAttributeType.CAN_NOT_CHANGE_OWNER, TypeInitializerAttributeType.HAS_AUDIT,
                    TypeInitializerAttributeType.HAS_CONTENT, TypeInitializerAttributeType.HAS_PERMISSION,
                    TypeInitializerAttributeType.HAS_PROPERTIES, TypeInitializerAttributeType.TEMPORARY);
            Set<UUID> childTypes = Set.of();
            AInfoTypeInitializer typeInitializer = this.factoryManager.create(PortTypeInitializer.class);

            typeManager.create(kernelConfiguration.PROCESSES_COMMUNICATION_INSTANCE_PORT_ID,
                    kernelConfiguration.PROCESSES_COMMUNICATION_INSTANCE_PORT_NAME, attribute, childTypes, typeInitializer);

            typeInitializer = this.factoryManager.create(SignalTypeInitializer.class);

            typeManager.create(kernelConfiguration.PROCESSES_COMMUNICATION_INSTANCE_SIGNAL_ID,
                    kernelConfiguration.PROCESSES_COMMUNICATION_INSTANCE_SIGNAL_NAME, attribute, childTypes, typeInitializer);

            typeInitializer = this.factoryManager.create(SessionTypeInitializer.class);

            typeManager.create(kernelConfiguration.PROCESSES_SESSION_INSTANCE_ID,
                    kernelConfiguration.PROCESSES_SESSION_INSTANCE_NAME, attribute, childTypes, typeInitializer);
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
        ThreadStatusObject threadStatus = thread.getStatus();

        if (LogicalUtil.allNotEqual(threadStatus.get(), ThreadStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

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
                && !(ObjectUtil.allNotNull(accountAuthorization) && accountAuthorization.checkAndGetSummary().getID().equals(processToken.getAccountID())))
                && (!ValueUtil.isAnyNullOrEmpty(currentProcessSession.getID()) && !currentProcessSession.getID().equals(processSession.getID()))) {
            throw new ConditionRefuseException();
        }

        return process;
    }

    public ProcessObject get(UUID processID) {
        return this.get(processID, null);
    }

    public ProcessObject create(AccountAuthorizationObject accountAuthorization, UUID fileIndex, String parameters, List<IdentificationDefinition> workFolder) {
        ProcessCreatorDefinition processCreator = new ProcessCreatorDefinition();

        if (ObjectUtil.allNotNull(accountAuthorization)) {
            processCreator.setAccountAuthorization(accountAuthorization);
        }

        processCreator.setFileIndex(fileIndex);

        if (!ValueUtil.isAnyNullOrEmpty(parameters)) {
            processCreator.setParameters(parameters);
        } else {
            processCreator.setParameters(StringUtil.EMPTY);
        }
        if (ObjectUtil.allNotNull(workFolder) && !workFolder.isEmpty()) {
            processCreator.setWorkFolder(workFolder);
        }

        ProcessObject process = this.getCurrent();
        ProcessCreateBuilder processCreateBuilder = this.factory.createProcessCreator(process);

        return processCreateBuilder.build(processCreator);
    }

    public void endCurrent() {
        ProcessObject process = this.getCurrent();
        ProcessObject parentProcess = null;

        if (!ValueUtil.isAnyNullOrEmpty(process.getParentID())) {
            try {
                parentProcess = this.getTarget(process.getParentID());
            } catch (StatusNotExistedException ignored) {
            }
        }

        ProcessEndBuilder processEndBuilder = this.factory.createProcessEnd(parentProcess, process);
        processEndBuilder.build();
    }
}
