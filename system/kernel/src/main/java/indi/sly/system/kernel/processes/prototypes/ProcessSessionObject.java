package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.values.MethodScopeType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.instances.prototypes.SessionContentObject;
import indi.sly.system.kernel.processes.values.ProcessSessionDefinition;
import indi.sly.system.kernel.processes.values.ProcessStatusType;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.AccountObject;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;
import indi.sly.system.kernel.security.values.AccessControlDefinition;
import indi.sly.system.kernel.security.values.AccessControlScopeType;
import indi.sly.system.kernel.security.values.PermissionType;
import indi.sly.system.kernel.security.values.UserType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessSessionObject extends ABytesValueProcessObject<ProcessSessionDefinition, ProcessObject> {
    public void create(String name, long type) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account = userManager.getCurrentAccount();
        UUID accountID = account.getID();
        String accountName = account.getName();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        UUID sessionID;

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (!ValueUtil.isAnyNullOrEmpty(this.value.getSessionID())) {
                throw new StatusAlreadyFinishedException();
            }

            InfoObject sessionsInfo = objectManager.get(List.of(new IdentificationDefinition("Sessions"),
                    new IdentificationDefinition(accountName)));
            InfoObject sessionInfo = sessionsInfo.createChildAndOpen(kernelConfiguration.PROCESSES_SESSION_INSTANCE_ID,
                    new IdentificationDefinition(UUID.randomUUID()), InfoOpenAttributeType.OPEN_SHARED_WRITE);

            SecurityDescriptorObject securityDescriptor = sessionInfo.getSecurityDescriptor();
            Set<AccessControlDefinition> permissions = new HashSet<>();
            AccessControlDefinition permission = new AccessControlDefinition();
            permission.getUserID().setID(accountID);
            permission.getUserID().setType(UserType.ACCOUNT);
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(LogicalUtil.or(PermissionType.LISTCHILD_READDATA_ALLOW, PermissionType.CREATECHILD_WRITEDATA_ALLOW));
            permissions.add(permission);
            permission = new AccessControlDefinition();
            permission.getUserID().setID(sessionInfo.getID());
            permission.getUserID().setType(UserType.SESSION);
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(LogicalUtil.or(PermissionType.LISTCHILD_READDATA_ALLOW, PermissionType.CREATECHILD_WRITEDATA_ALLOW));
            permissions.add(permission);
            securityDescriptor.setPermissions(permissions);

            SessionContentObject sessionContent = (SessionContentObject) sessionInfo.getContent();
            sessionContent.setName(name);
            sessionContent.setType(type);
            sessionContent.addProcessID(this.parent.getID());

            ProcessInfoTableObject processInfoTable = this.parent.getInfoTable();
            ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(sessionInfo.getID());
            processInfoEntry.setUnsupportedDelete(true);

            sessionID = sessionInfo.getID();

            this.value.setAccountName(accountName);
            this.value.setSessionID(sessionID);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void close() {
        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            String accountName = this.value.getAccountName();
            UUID sessionID = this.value.getSessionID();

            if (StringUtil.isNameIllegal(accountName) || ValueUtil.isAnyNullOrEmpty(sessionID)) {
                throw new StatusRelationshipErrorException();
            }

            SessionContentObject sessionContent = this.getContent(accountName, sessionID);
            if (ObjectUtil.allNotNull(sessionContent)) {
                sessionContent.deleteProcessID(this.parent.getID());
            }

            ProcessInfoTableObject processInfoTable = this.parent.getInfoTable();
            if (processInfoTable.containByID(sessionID)) {
                ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(sessionID);
                processInfoEntry.setUnsupportedDelete(false);

                InfoObject info = processInfoEntry.getInfo();
                info.close();
            }

            this.value.setAccountName(null);
            this.value.setSessionID(null);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public UUID getID() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getSessionID();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public void inheritID() {
        if (this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();

        if (!process.getID().equals(parent.getParentID())) {
            throw new ConditionRefuseException();
        }

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account = userManager.getCurrentAccount();
        String accountName = account.getName();

        ProcessSessionObject processSession = process.getSession();
        UUID sessionID = processSession.getID();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (!ValueUtil.isAnyNullOrEmpty(sessionID)) {
                List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Sessions"),
                        new IdentificationDefinition(accountName), new IdentificationDefinition(sessionID));

                InfoObject sessionInfo = objectManager.get(identifications);

                sessionInfo.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);

                SessionContentObject sessionContent = (SessionContentObject) sessionInfo.getContent();
                sessionContent.addProcessID(this.parent.getID());

                ProcessInfoTableObject processInfoTable = this.parent.getInfoTable();
                ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(sessionInfo.getID());
                processInfoEntry.setUnsupportedDelete(true);
            }

            this.value.setAccountName(accountName);
            this.value.setSessionID(sessionID);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public void setID(UUID sessionID) {
        if (ValueUtil.isAnyNullOrEmpty(sessionID)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account = userManager.getCurrentAccount();
        String accountName = account.getName();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        try {
            this.lock(LockType.WRITE);
            this.init();

            InfoObject newSessionInfo = objectManager.get(List.of(new IdentificationDefinition("Sessions"),
                    new IdentificationDefinition(accountName), new IdentificationDefinition(sessionID)));
            newSessionInfo.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);
            SessionContentObject newSessionContent = (SessionContentObject) newSessionInfo.getContent();
            newSessionContent.addProcessID(this.parent.getID());

            String oldAccountName = this.value.getAccountName();
            UUID oldSessionID = this.value.getSessionID();

            if (!StringUtil.isNameIllegal(accountName) && !ValueUtil.isAnyNullOrEmpty(oldSessionID)) {
                SessionContentObject oldSessionContent = this.getContent(oldAccountName, oldSessionID);
                if (ObjectUtil.allNotNull(oldSessionContent)) {
                    oldSessionContent.deleteProcessID(this.parent.getID());
                }

                ProcessInfoTableObject processInfoTable = this.parent.getInfoTable();
                if (processInfoTable.containByID(oldSessionID)) {
                    ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(oldSessionID);
                    processInfoEntry.setUnsupportedDelete(false);

                    InfoObject info = processInfoEntry.getInfo();
                    info.close();
                }

                this.value.setAccountName(null);
                this.value.setSessionID(null);
            }

            ProcessInfoTableObject processInfoTable = this.parent.getInfoTable();
            ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(newSessionInfo.getID());
            processInfoEntry.setUnsupportedDelete(true);

            this.value.setAccountName(accountName);
            this.value.setSessionID(sessionID);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    private SessionContentObject getContent(String accountName, UUID sessionID) {
        if (StringUtil.isNameIllegal(accountName) || ValueUtil.isAnyNullOrEmpty(sessionID)) {
            throw new ConditionParametersException();
        }

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Sessions"),
                new IdentificationDefinition(accountName), new IdentificationDefinition(sessionID));

        InfoObject sessionInfo = objectManager.get(identifications);

        SessionContentObject sessionContent;
        try {
            sessionContent = (SessionContentObject) sessionInfo.getContent();
        } catch (AKernelException ignored) {
            sessionContent = null;
        }

        return sessionContent;
    }

    public SessionContentObject getContent() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.READ);
            this.init();

            String accountName = this.value.getAccountName();
            UUID sessionID = this.value.getSessionID();

            if (StringUtil.isNameIllegal(accountName) || ValueUtil.isAnyNullOrEmpty(sessionID)) {
                throw new StatusRelationshipErrorException();
            }

            return this.getContent(accountName, sessionID);
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
