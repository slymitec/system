package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.instances.prototypes.SessionContentObject;
import indi.sly.system.kernel.processes.instances.values.SessionType;
import indi.sly.system.kernel.processes.values.ApplicationDefinition;
import indi.sly.system.kernel.processes.values.ProcessContextType;
import indi.sly.system.kernel.processes.values.ProcessStatusType;
import indi.sly.system.kernel.processes.values.ProcessTokenDefinition;
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
public class ProcessTokenObject extends ABytesValueProcessObject<ProcessTokenDefinition, ProcessObject> {
    public void setAccountAuthorization(AccountAuthorizationObject accountAuthorization) {
        if (ObjectUtil.isAnyNull(accountAuthorization)) {
            throw new ConditionParametersException();
        }

        if (this.parent.isCurrent()) {
            if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING)) {
                throw new StatusRelationshipErrorException();
            }
        } else {
            if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
                throw new StatusRelationshipErrorException();
            }

            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();

            if (!process.getID().equals(parent.getParentID())) {
                throw new ConditionRefuseException();
            }
        }

        KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

        AccountAuthorizationSummaryDefinition accountAuthorizationSummary = accountAuthorization.checkAndGetSummary();

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (!ValueUtil.isAnyNullOrEmpty(this.value.getAccountID())
                    && !this.value.getAccountID().equals(accountAuthorizationSummary.getID())) {
                throw new ConditionRefuseException();
            }

            this.value.setAccountID(accountAuthorizationSummary.getID());
            AccountAuthorizationTokenDefinition accountAuthorizationToken = accountAuthorizationSummary.getToken();
            this.value.setPrivileges(accountAuthorizationToken.getPrivileges());
            Map<Long, Integer> processTokenLimits = this.value.getLimits();
            processTokenLimits.clear();
            processTokenLimits.putAll(accountAuthorizationToken.getLimits());
            Set<UUID> processTokenRoles = this.value.getRoles();
            processTokenRoles.clear();
            processTokenRoles.addAll(accountAuthorizationToken.getRoles());
            if (ValueUtil.isAnyNullOrEmpty(accountAuthorizationSummary.getPassword())) {
                processTokenRoles.add(kernelConfiguration.SECURITY_ROLE_EMPTY_PASSWORD_ID);
            }

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public UUID getAccountID() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getAccountID();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void inheritAccountID() {
        if (this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();

        if (!process.getID().equals(parent.getParentID())) {
            throw new ConditionRefuseException();
        }

        ProcessTokenObject processToken = process.getToken();

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.setAccountID(processToken.getAccountID());

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public long getPrivileges() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getPrivileges();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void inheritPrivileges() {
        if (this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();

        if (!process.getID().equals(parent.getParentID())) {
            throw new ConditionRefuseException();
        }

        ProcessTokenObject processToken = process.getToken();

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.setPrivileges(processToken.getPrivileges());

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void setPrivileges(long privileges) {
        if (this.parent.isCurrent()) {
            if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING)) {
                throw new StatusRelationshipErrorException();
            } else {
                if (!this.isPrivileges(PrivilegeType.CORE_MODIFY_PRIVILEGES)) {
                    throw new ConditionRefuseException();
                }
            }
        } else {
            if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
                throw new StatusRelationshipErrorException();
            }

            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessTokenObject processToken = process.getToken();

            if (!process.getID().equals(parent.getParentID())) {
                throw new ConditionRefuseException();
            }
            if (!processToken.isPrivileges(PrivilegeType.CORE_MODIFY_PRIVILEGES)) {
                throw new ConditionRefuseException();
            }
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.setPrivileges(privileges);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public boolean isPrivileges(long privileges) {
        return LogicalUtil.isAllExist(this.getPrivileges(), privileges);
    }

    public Map<Long, Integer> getLimits() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.READ);
            this.init();

            return CollectionUtil.unmodifiable(this.value.getLimits());
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void inheritLimits() {
        if (this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();

            if (!process.getID().equals(parent.getParentID())) {
                throw new ConditionRefuseException();
            }

            ProcessTokenObject processToken = process.getToken();

            Map<Long, Integer> processTokenLimits = this.getLimits();
            processTokenLimits.clear();
            processTokenLimits.putAll(processToken.getLimits());

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void setLimits(Map<Long, Integer> limits) {
        if (ObjectUtil.isAnyNull(limits)) {
            throw new ConditionParametersException();
        }

        if (this.parent.isCurrent()) {
            if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING)) {
                throw new StatusRelationshipErrorException();
            } else {
                if (!this.isPrivileges(PrivilegeType.PROCESSES_MODIFY_LIMITS)) {
                    throw new ConditionRefuseException();
                }
            }
        } else {
            if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
                throw new StatusRelationshipErrorException();
            }

            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessTokenObject processToken = process.getToken();

            if (!process.getID().equals(parent.getParentID())) {
                throw new ConditionRefuseException();
            }
            if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_LIMITS)) {
                throw new ConditionRefuseException();
            }
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Map<Long, Integer> processTokenLimits = this.getLimits();
            processTokenLimits.clear();
            processTokenLimits.putAll(limits);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public Set<UUID> getRoles() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.READ);
            this.init();

            return CollectionUtil.unmodifiable(this.value.getRoles());
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void initDefaultRoles() {
        if (this.parent.isCurrent()) {
            if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING)) {
                throw new StatusRelationshipErrorException();
            }
        } else {
            if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
                throw new StatusRelationshipErrorException();
            }

            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();

            if (!process.getID().equals(parent.getParentID())) {
                throw new ConditionRefuseException();
            }
        }

        KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

        ProcessContextObject processContext = this.parent.getContext();

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
        if (this.parent.isCurrent()) {
            processSession = this.parent.getSession();
        } else {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            processSession = process.getSession();
        }
        if (!ValueUtil.isAnyNullOrEmpty(processSession.getID())) {
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

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (processToken.getAccountID().equals(this.value.getAccountID())) {
            UserManager userManager = this.factoryManager.getManager(UserManager.class);

            AccountObject account = userManager.getCurrentAccount();
            if (ValueUtil.isAnyNullOrEmpty(account.getPassword())) {
                roles.add(kernelConfiguration.SECURITY_ROLE_EMPTY_PASSWORD_ID);
            }
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.getRoles().clear();
            this.value.getRoles().addAll(roles);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
