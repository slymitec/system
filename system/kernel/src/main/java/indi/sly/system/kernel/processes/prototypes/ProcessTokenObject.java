package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionContextException;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.prototypes.AChildCacheableObject;
import indi.sly.system.kernel.core.prototypes.IByteValueSupporter;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.instances.prototypes.SessionContentObject;
import indi.sly.system.kernel.processes.instances.values.SessionType;
import indi.sly.system.kernel.processes.lang.ProcessProcessorReadComponentFunction;
import indi.sly.system.kernel.processes.lang.ProcessProcessorWriteComponentConsumer;
import indi.sly.system.kernel.processes.prototypes.mediators.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.*;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.prototypes.AccountObject;
import indi.sly.system.kernel.security.values.AccountAuthorizationSummaryDefinition;
import indi.sly.system.kernel.security.values.AccountAuthorizationTokenDefinition;
import indi.sly.system.kernel.security.values.PrivilegeType;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessTokenObject extends AChildCacheableObject<ProcessChildCacheEntity, ProcessObject> implements IByteValueSupporter<ProcessTokenDefinition> {
    protected ProcessFactory factory;
    protected ProcessProcessorMediator processorMediator;

    private ProcessEntity getSelf() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getProcess().getProcessId())) {
            throw new ConditionContextException();
        }

        return this.processorMediator.getSelf().apply(this.cache.getProcess().getProcessId());
    }

    private ProcessTokenDefinition init(ProcessEntity process) {
        Set<ProcessProcessorReadComponentFunction> resolvers = this.processorMediator.getReadProcessTokens();

        byte[] source = null;

        for (ProcessProcessorReadComponentFunction resolver : resolvers) {
            source = resolver.apply(source, process);
        }

        return IByteValueSupporter.super.init(source);
    }

    private void flush(ProcessEntity process, ProcessTokenDefinition value) {
        byte[] source = IByteValueSupporter.super.flush(value);

        Set<ProcessProcessorWriteComponentConsumer> resolvers = this.processorMediator.getWriteProcessTokens();

        for (ProcessProcessorWriteComponentConsumer resolver : resolvers) {
            resolver.accept(process, source);
        }
    }

    public void injectAccountAuthorization(AccountAuthorizationObject accountAuthorization) {
        if (ObjectUtil.isAnyNull(accountAuthorization)) {
            throw new ConditionParametersException();
        }

        if (this.base.isCurrent()) {
            if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.RUNNING)) {
                throw new StatusRelationshipErrorException();
            }
        } else {
            if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
                throw new StatusRelationshipErrorException();
            }

            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();

            if (!process.getId().equals(base.getParentId())) {
                throw new ConditionRefuseException();
            }
        }

        KernelConfigurationDefinition kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

        AccountAuthorizationSummaryDefinition accountAuthorizationSummary = accountAuthorization.checkAndGetSummary();

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessTokenDefinition processToken = this.init(process);

            if (!ValueUtil.isAnyNullOrEmpty(processToken.getAccountId())
                    && !processToken.getAccountId().equals(accountAuthorizationSummary.getID())) {
                throw new ConditionRefuseException();
            }

            processToken.setAccountId(accountAuthorizationSummary.getID());
            AccountAuthorizationTokenDefinition accountAuthorizationToken = accountAuthorizationSummary.getToken();
            processToken.setPrivileges(accountAuthorizationToken.getPrivileges());
            Map<Long, Integer> processTokenLimits = processToken.getLimits();
            processTokenLimits.clear();
            processTokenLimits.putAll(accountAuthorizationToken.getLimits());
            Set<UUID> processTokenRoles = processToken.getRoles();
            processTokenRoles.clear();
            processTokenRoles.addAll(accountAuthorizationToken.getRoles());
            if (ValueUtil.isAnyNullOrEmpty(accountAuthorizationSummary.getPassword())) {
                processTokenRoles.add(kernelConfiguration.SECURITY_ROLE_EMPTY_PASSWORD_ID);
            }

            this.flush(process, processToken);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public UUID getAccountId() {
        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessTokenDefinition processToken = this.init(process);

            return processToken.getAccountId();
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public void inheritAccountID() {
        if (this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();

        if (!currentProcess.getId().equals(this.base.getParentId())) {
            throw new ConditionRefuseException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessTokenDefinition processToken = this.init(process);

            processToken.setAccountId(currentProcessToken.getAccountId());

            this.flush(process, processToken);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public long getPrivileges() {
        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessTokenDefinition processToken = this.init(process);

            return processToken.getPrivileges();
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public void inheritPrivileges() {
        if (this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();

        if (!currentProcess.getId().equals(this.base.getParentId())) {
            throw new ConditionRefuseException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessTokenDefinition processToken = this.init(process);

            processToken.setPrivileges(currentProcessToken.getPrivileges());

            this.flush(process, processToken);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void setPrivileges(long privileges) {
        if (this.base.isCurrent()) {
            if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.RUNNING)) {
                throw new StatusRelationshipErrorException();
            } else {
                if (!this.isPrivileges(PrivilegeType.CORE_MODIFY_PRIVILEGES)) {
                    throw new ConditionRefuseException();
                }
            }
        } else {
            if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
                throw new StatusRelationshipErrorException();
            }

            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject currentProcess = processManager.getCurrent();
            ProcessTokenObject currentProcessToken = currentProcess.getToken();

            if (!currentProcess.getId().equals(this.base.getParentId())) {
                throw new ConditionRefuseException();
            }
            if (!currentProcessToken.isPrivileges(PrivilegeType.CORE_MODIFY_PRIVILEGES)) {
                throw new ConditionRefuseException();
            }
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessTokenDefinition processToken = this.init(process);

            processToken.setPrivileges(privileges);

            this.flush(process, processToken);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public boolean isPrivileges(long privileges) {
        return LogicalUtil.isAllExist(this.getPrivileges(), privileges);
    }

    public Map<Long, Integer> getLimits() {
        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessTokenDefinition processToken = this.init(process);

            return CollectionUtil.unmodifiable(processToken.getLimits());
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public void inheritLimits() {
        if (this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();

        if (!currentProcess.getId().equals(this.base.getParentId())) {
            throw new ConditionRefuseException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessTokenDefinition processToken = this.init(process);

            Map<Long, Integer> processTokenLimits = processToken.getLimits();
            processTokenLimits.clear();
            processTokenLimits.putAll(currentProcessToken.getLimits());

            this.flush(process, processToken);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void setLimits(Map<Long, Integer> limits) {
        if (ObjectUtil.isAnyNull(limits)) {
            throw new ConditionParametersException();
        }

        if (this.base.isCurrent()) {
            if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.RUNNING)) {
                throw new StatusRelationshipErrorException();
            } else {
                if (!this.isPrivileges(PrivilegeType.PROCESSES_MODIFY_LIMITS)) {
                    throw new ConditionRefuseException();
                }
            }
        } else {
            if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
                throw new StatusRelationshipErrorException();
            }

            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessTokenObject processToken = process.getToken();

            if (!process.getId().equals(this.base.getParentId())) {
                throw new ConditionRefuseException();
            }
            if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_LIMITS)) {
                throw new ConditionRefuseException();
            }
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessTokenDefinition processToken = this.init(process);

            Map<Long, Integer> processTokenLimits = processToken.getLimits();
            processTokenLimits.clear();
            processTokenLimits.putAll(limits);

            this.flush(process, processToken);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public Set<UUID> getRoles() {
        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessTokenDefinition processToken = this.init(process);

            return CollectionUtil.unmodifiable(processToken.getRoles());
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public void initDefaultRoles() {
        if (this.base.isCurrent()) {
            if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.RUNNING)) {
                throw new StatusRelationshipErrorException();
            }
        } else {
            if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
                throw new StatusRelationshipErrorException();
            }

            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();

            if (!process.getId().equals(this.base.getParentId())) {
                throw new ConditionRefuseException();
            }
        }

        KernelConfigurationDefinition kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

        ProcessContextObject processContext = this.base.getContext();

        Set<UUID> roles = new HashSet<>();
        long processContextType = processContext.getType();
        if (LogicalUtil.isAllExist(processContextType, ProcessContextType.SERVICE)) {
            ApplicationDefinition processContextApplication = processContext.getApplication();
            if (ObjectUtil.allNotNull(processContextApplication)) {
                roles.add(processContextApplication.getID());
            }
        } else if (LogicalUtil.isAllExist(processContextType, ProcessContextType.BATCH)) {
            roles.add(kernelConfiguration.SECURITY_ROLE_BATCHES_ID);
        } else if (LogicalUtil.isAllExist(processContextType, ProcessContextType.EXECUTABLE)) {
            roles.add(kernelConfiguration.SECURITY_ROLE_EXECUTABLE_ID);
        }

        ProcessSessionObject processSession;
        if (this.base.isCurrent()) {
            processSession = this.base.getSession();
        } else {
            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            processSession = process.getSession();
        }
        if (!ValueUtil.isAnyNullOrEmpty(processSession.getId())) {
            SessionContentObject sessionContent = processSession.getContent();
            if (ObjectUtil.allNotNull(sessionContent)) {
                long sessionContentType = sessionContent.getType();
                if (LogicalUtil.isAnyEqual(sessionContentType, SessionType.API)) {
                    roles.add(kernelConfiguration.SECURITY_ROLE_API_ID);
                } else if (LogicalUtil.isAnyEqual(sessionContentType, SessionType.GUI)) {
                    roles.add(kernelConfiguration.SECURITY_ROLE_GUI_ID);
                } else if (LogicalUtil.isAnyEqual(sessionContentType, SessionType.CLI)) {
                    roles.add(kernelConfiguration.SECURITY_ROLE_CLI_ID);
                }
            }
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessTokenDefinition processToken = this.init(process);

            if (currentProcessToken.getAccountId().equals(processToken.getAccountId())) {
                UserManager userManager = this.coreManager.getManager(UserManager.class);

                AccountObject account = userManager.getCurrentAccount();
                if (ValueUtil.isAnyNullOrEmpty(account.getPassword())) {
                    roles.add(kernelConfiguration.SECURITY_ROLE_EMPTY_PASSWORD_ID);
                }
            }

            Set<UUID> processTokenRoles = processToken.getRoles();

            processTokenRoles.clear();
            processTokenRoles.addAll(roles);

            this.flush(process, processToken);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }
}
