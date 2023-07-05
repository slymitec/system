package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.values.InfoWildcardDefinition;
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
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessSessionObject extends ABytesValueProcessObject<ProcessSessionDefinition, ProcessObject> {
    public UUID getID() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getSessionID();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void create(String name, long type) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account = userManager.getCurrentAccount();
        UUID accountID = account.getID();

        try {
            this.lock(LockType.WRITE);
            this.init();

            UUID sessionID = this.value.getSessionID();

            if (this.value.isLink()) {
                throw new StatusRelationshipErrorException();
            }

            InfoObject sessionsInfo = objectManager.get(List.of(new IdentificationDefinition("Sessions")));

            if (!ValueUtil.isAnyNullOrEmpty(sessionID)) {
                Set<InfoSummaryDefinition> infoSummary = sessionsInfo.queryChild(new InfoWildcardDefinition(sessionID));
                if (!infoSummary.isEmpty()) {
                    throw new StatusAlreadyExistedException();
                }
            }

            InfoObject sessionInfo = sessionsInfo.createChildAndOpen(kernelConfiguration.PROCESSES_SESSION_INSTANCE_ID,
                    new IdentificationDefinition(UUID.randomUUID()), InfoOpenAttributeType.OPEN_SHARED_WRITE);
            sessionID = sessionInfo.getID();

            SecurityDescriptorObject securityDescriptor = sessionInfo.getSecurityDescriptor();
            Set<AccessControlDefinition> permissions = new HashSet<>();
            AccessControlDefinition permission = new AccessControlDefinition();
            permission.getUserID().setID(accountID);
            permission.getUserID().setType(UserType.ACCOUNT);
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(LogicalUtil.or(PermissionType.LISTCHILD_READDATA_ALLOW, PermissionType.CREATECHILD_WRITEDATA_ALLOW));
            permissions.add(permission);
            permission = new AccessControlDefinition();
            permission.getUserID().setID(sessionID);
            permission.getUserID().setType(UserType.SESSION);
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(LogicalUtil.or(PermissionType.LISTCHILD_READDATA_ALLOW, PermissionType.CREATECHILD_WRITEDATA_ALLOW));
            permissions.add(permission);
            securityDescriptor.setPermissions(permissions);

            SessionContentObject sessionContent = (SessionContentObject) sessionInfo.getContent();
            sessionContent.setName(name);
            sessionContent.setType(type);

            ProcessInfoTableObject processInfoTable = this.parent.getInfoTable();
            ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(sessionInfo.getID());
            processInfoEntry.setUnsupportedDelete(true);

            sessionID = sessionInfo.getID();

            this.value.setSessionID(sessionID);
            this.value.setLink(true);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void close() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            UUID sessionID = this.value.getSessionID();

            if (!this.value.isLink() || ValueUtil.isAnyNullOrEmpty(sessionID)) {
                throw new StatusRelationshipErrorException();
            }

            ProcessInfoTableObject processInfoTable = this.parent.getInfoTable();
            if (processInfoTable.containByID(sessionID)) {
                ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(sessionID);
                processInfoEntry.setUnsupportedDelete(false);

                InfoObject info = processInfoEntry.getInfo();
                info.close();
            }

            this.value.setSessionID(null);
            this.value.setLink(false);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void inheritID() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        if (!process.getID().equals(parent.getParentID())) {
            throw new ConditionRefuseException();
        }

        ProcessSessionObject processSession = process.getSession();
        UUID sessionID = processSession.getID();

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (this.value.isLink()) {
                throw new StatusRelationshipErrorException();
            }

            this.value.setSessionID(sessionID);
            this.value.setLink(false);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void link() {
        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        try {
            this.lock(LockType.WRITE);
            this.init();

            UUID sessionID = this.value.getSessionID();

            if (this.value.isLink()) {
                throw new StatusAlreadyFinishedException();
            }
            if (ValueUtil.isAnyNullOrEmpty(sessionID)) {
                throw new StatusRelationshipErrorException();
            }

            InfoObject sessionInfo = objectManager.get(List.of(new IdentificationDefinition("Sessions"),
                    new IdentificationDefinition(sessionID)));
            sessionInfo.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);

            ProcessInfoTableObject processInfoTable = this.parent.getInfoTable();
            ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(sessionInfo.getID());
            processInfoEntry.setUnsupportedDelete(true);

            this.value.setSessionID(sessionID);
            this.value.setLink(true);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public boolean isLink() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.isLink();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public SessionContentObject getContent() {
        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.READ);
            this.init();

            UUID sessionID = this.value.getSessionID();

            if (!this.value.isLink() || ValueUtil.isAnyNullOrEmpty(sessionID)) {
                throw new StatusRelationshipErrorException();
            }

            ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

            List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Sessions"),
                    new IdentificationDefinition(sessionID));

            InfoObject sessionInfo = objectManager.get(identifications);

            SessionContentObject sessionContent;
            try {
                sessionContent = (SessionContentObject) sessionInfo.getContent();
            } catch (AKernelException ignored) {
                sessionContent = null;
            }

            return sessionContent;
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
