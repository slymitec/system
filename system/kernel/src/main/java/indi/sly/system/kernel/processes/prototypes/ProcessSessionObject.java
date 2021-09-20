package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.lang.StatusAlreadyFinishedException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.prototypes.AValueProcessObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.SessionManager;
import indi.sly.system.kernel.processes.instances.prototypes.SessionContentObject;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.values.ProcessStatusType;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;
import indi.sly.system.kernel.security.values.AccessControlDefinition;
import indi.sly.system.kernel.security.values.AccessControlScopeType;
import indi.sly.system.kernel.security.values.PermissionType;
import indi.sly.system.kernel.security.values.UserType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessSessionObject extends AValueProcessObject<ProcessEntity, ProcessObject> {
    public void create() {
        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessTokenObject processToken = this.parent.getToken();

        KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Sessions"));

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (!ValueUtil.isAnyNullOrEmpty(this.value.getSessionID())) {
                throw new StatusAlreadyFinishedException();
            }

            InfoObject sessionsInfo = objectManager.get(identifications);

            InfoObject sessionInfo = sessionsInfo.createChildAndOpen(kernelConfiguration.PROCESSES_SESSION_INSTANCE_ID,
                    new IdentificationDefinition(UUID.randomUUID()), InfoOpenAttributeType.OPEN_SHARED_WRITE);

            SecurityDescriptorObject securityDescriptor = sessionInfo.getSecurityDescriptor();
            Set<AccessControlDefinition> permissions = new HashSet<>();
            AccessControlDefinition permission = new AccessControlDefinition();
            permission.getUserID().setID(this.parent.getID());
            permission.getUserID().setType(UserType.PROCESS);
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(PermissionType.FULLCONTROL_ALLOW);
            permissions.add(permission);
            permission = new AccessControlDefinition();
            permission.getUserID().setID(processToken.getAccountID());
            permission.getUserID().setType(UserType.ACCOUNT);
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
            permissions.add(permission);
            securityDescriptor.setPermissions(permissions);

            SessionContentObject sessionContent = (SessionContentObject) sessionInfo.getContent();
            //signalContent.setSourceProcessIDs(sourceProcessIDs);

            ProcessInfoTableObject processInfoTable = this.parent.getInfoTable();
            ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(sessionInfo.getID());
            processInfoEntry.setUnsupportedDelete(true);

            this.value.setSessionID(sessionInfo.getID());

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void delete() {
        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            UUID sessionID = this.value.getSessionID();

            if (ValueUtil.isAnyNullOrEmpty(sessionID)) {
                throw new StatusAlreadyFinishedException();
            }

            ProcessInfoTableObject processInfoTable = this.parent.getInfoTable();
            if (processInfoTable.containByID(sessionID)) {
                ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(sessionID);
                processInfoEntry.setUnsupportedDelete(false);

                InfoObject info = processInfoEntry.getInfo();
                info.close();
            }

            this.value.setSessionID(null);

            this.fresh();
            this.lock(LockType.NONE);
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

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.setSessionID(process.getSession().getID());

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void setID(UUID sessionID) {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Sessions"),
                new IdentificationDefinition(sessionID));

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        try {
            this.lock(LockType.WRITE);
            this.init();

            InfoObject sessionInfo = objectManager.get(identifications);



            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        SessionManager sessionManager = new SessionManager();
        SessionContentObject sessionContent = sessionManager.getAndOpen(sessionID);
        if (!sessionContent.getAccountID().equals(processToken.getAccountID())) {
            throw new ConditionRefuseException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.setSessionID(sessionID);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public Map<String, String> getEnvironmentVariables() {
        return null;
    }

    public long getType() {
        return 0;
    }
}
