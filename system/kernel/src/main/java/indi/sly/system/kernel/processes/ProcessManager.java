package indi.sly.system.kernel.processes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.prototypes.*;
import indi.sly.system.kernel.processes.values.ProcessAdditionalCreatorDefinition;
import indi.sly.system.kernel.processes.values.ProcessCreatorDefinition;
import indi.sly.system.kernel.processes.values.ThreadStatusType;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessManager extends AManager {
    private ProcessFactory factory;

    public ProcessFactory getFactory() {
        return this.factory;
    }

    @Override
    public void startup(long startup) {
        if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_SELF)) {
            this.factory = this.coreManager.create(ProcessFactory.class);
            this.factory.init();
        } else if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_KERNEL)) {
        }
    }

    @Override
    public void shutdown() {
    }

    private ProcessObject getTarget(UUID processId) {
        if (ValueUtil.isAnyNullOrEmpty(processId)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

        return this.factory.buildProcess(processRepository.get(processId));
    }

    public ProcessObject getCurrent() {
        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);

        ThreadObject thread = threadManager.getCurrent();
        ThreadStatusObject threadStatus = thread.getStatus();

        if (LogicalUtil.allNotEqual(threadStatus.get(), ThreadStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        return this.getTarget(thread.getProcessId());
    }

    public ProcessObject getWithAuthorization(UUID processId, AccountAuthorizationObject accountAuthorization) {
        if (ValueUtil.isAnyNullOrEmpty(processId)) {
            throw new ConditionParametersException();
        }

        ProcessObject currentProcess = this.getCurrent();
        if (currentProcess.getId().equals(processId)) {
            return currentProcess;
        }

        ProcessObject process = this.getTarget(processId);

        ProcessSessionObject processSession = process.getSession();
        ProcessTokenObject processToken = process.getToken();
        ProcessSessionObject currentProcessSession = currentProcess.getSession();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();

        if (!currentProcessToken.getAccountId().equals(processToken.getAccountId())
                && (!currentProcessToken.isPrivileges(PrivilegeType.SECURITY_DO_WITH_ANY_ACCOUNT)
                && !(ObjectUtil.allNotNull(accountAuthorization) && accountAuthorization.checkAndGetSummary().getID().equals(processToken.getAccountId())))
                && (!ValueUtil.isAnyNullOrEmpty(currentProcessSession.getId()) && !currentProcessSession.getId().equals(processSession.getId()))) {
            throw new ConditionRefuseException();
        }

        return process;
    }

    public ProcessObject get(UUID processId) {
        return this.getWithAuthorization(processId, null);
    }

    public ProcessObject create(AccountAuthorizationObject accountAuthorization, UUID fileIndex, String parameters, PathRecord workFolder, ProcessAdditionalCreatorDefinition additionalCreator) {
        if (ValueUtil.isAnyNullOrEmpty(fileIndex)) {
            throw new ConditionParametersException();
        }

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
        if (ObjectUtil.allNotNull(workFolder) && !workFolder.identifiers().isEmpty()) {
            processCreator.setWorkFolder(workFolder);
        }

        if (ObjectUtil.allNotNull(additionalCreator)) {
            processCreator.setInheritSession(additionalCreator.isInheritSession());
            processCreator.setContextType(additionalCreator.getContextType());
        }

        ProcessObject process = this.getCurrent();
        ProcessCreateBuilder processCreateBuilder = this.factory.createProcessCreator(process);

        return processCreateBuilder.build(processCreator);
    }

    public void endCurrent() {
        ProcessObject process = this.getCurrent();
        ProcessObject parentProcess = null;

        if (!ValueUtil.isAnyNullOrEmpty(process.getParentId())) {
            try {
                parentProcess = this.getTarget(process.getParentId());
            } catch (StatusNotExistedException _) {
            }
        }

        ProcessEndBuilder processEndBuilder = this.factory.createProcessEnd(parentProcess, process);
        processEndBuilder.build();
    }

    public void end(UUID processId) {
        if (ValueUtil.isAnyNullOrEmpty(processId)) {
            throw new ConditionParametersException();
        }

        ProcessObject process = this.getTarget(processId);
        ProcessObject parentProcess = null;

        if (!ValueUtil.isAnyNullOrEmpty(process.getParentId())) {
            try {
                parentProcess = this.getTarget(process.getParentId());
            } catch (StatusNotExistedException _) {
            }
        }

        ProcessEndBuilder processEndBuilder = this.factory.createProcessEnd(parentProcess, process);
        processEndBuilder.build();
    }
}
