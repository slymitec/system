package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.instances.prototypes.AuditContentObject;
import indi.sly.system.kernel.security.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SecurityDescriptorObject extends ABytesValueProcessObject<SecurityDescriptorDefinition, SecurityDescriptorObject> {
    public SecurityDescriptorObject() {
        this.identifications = new ArrayList<>();
    }

    private final List<IdentificationDefinition> identifications;
    private boolean permission;
    private boolean audit;

    public void setIdentifications(List<IdentificationDefinition> identifications) {
        if (ObjectUtil.isAnyNull(identifications)) {
            throw new ConditionParametersException();
        }

        this.identifications.clear();
        this.identifications.addAll(identifications);
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    public void setAudit(boolean audit) {
        this.audit = audit;
    }

    private ProcessObject getCurrentProcess() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        return processManager.getCurrent();
    }

    private ProcessTokenObject getCurrentProcessToken() {
        ProcessObject process = this.getCurrentProcess();

        return process.getToken();
    }

    public List<SecurityDescriptorSummaryDefinition> getSummary() {
        if (!this.permission && !this.audit) {
            throw new StatusDisabilityException();
        }

        List<SecurityDescriptorSummaryDefinition> securityDescriptorSummaries = new ArrayList<>();

        try {
            this.lock(LockType.READ);
            this.init();

            ProcessTokenObject processToken = this.getCurrentProcessToken();

            if (this.permission) {
                if (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                        && !this.value.getOwners().contains(processToken.getAccountID())
                        && this.denyPermission(PermissionType.READPERMISSIONDESCRIPTOR_ALLOW, null)) {
                    throw new ConditionPermissionException();
                }
            }

            if (this.audit) {
                this.checkAudit(AuditType.READPERMISSIONDESCRIPTOR);
            }

            SecurityDescriptorObject securityDescriptor = this;
            do {
                SecurityDescriptorSummaryDefinition securityDescriptorSummary = new SecurityDescriptorSummaryDefinition();
                if (securityDescriptor.permission) {
                    if (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                            && !securityDescriptor.value.getOwners().contains(processToken.getAccountID())
                            && securityDescriptor.denyPermission(PermissionType.READPERMISSIONDESCRIPTOR_ALLOW, null)) {
                        break;
                    }

                    securityDescriptorSummary.setPermission(true);
                    securityDescriptorSummary.setInherit(securityDescriptor.value.isInherit());
                    securityDescriptorSummary.getPermissions().addAll(securityDescriptor.value.getPermissions());
                } else {
                    securityDescriptorSummary.setPermission(false);
                }
                if (securityDescriptor.audit) {
                    securityDescriptorSummary.setAudit(true);
                    securityDescriptorSummary.getAudits().addAll(securityDescriptor.value.getAudits());
                } else {
                    securityDescriptorSummary.setAudit(false);
                }
                securityDescriptorSummary.getIdentifications().addAll(securityDescriptor.identifications);

                securityDescriptorSummaries.add(securityDescriptorSummary);

                securityDescriptor = securityDescriptor.parent;
            } while (securityDescriptor != null);
            Collections.reverse(securityDescriptorSummaries);
        } finally {
            this.lock(LockType.NONE);
        }

        return CollectionUtil.unmodifiable(securityDescriptorSummaries);
    }

    public boolean isInherit() {
        if (!this.permission) {
            throw new StatusDisabilityException();
        }

        try {
            this.lock(LockType.READ);
            this.init();

            ProcessTokenObject processToken = this.getCurrentProcessToken();

            if (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                    && !this.value.getOwners().contains(processToken.getAccountID())
                    && this.denyPermission(PermissionType.READPERMISSIONDESCRIPTOR_ALLOW, null)) {
                throw new ConditionPermissionException();
            }

            if (this.audit) {
                this.checkAudit(AuditType.READPERMISSIONDESCRIPTOR);
            }

            return this.value.isInherit();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void setInherit(boolean inherit) {
        if (!this.permission) {
            throw new StatusDisabilityException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                    && !this.value.getOwners().contains(processToken.getAccountID())
                    && this.denyPermission(PermissionType.CHANGEPERMISSIONDESCRIPTOR_ALLOW, null)) {
                throw new ConditionPermissionException();
            }

            if (this.audit) {
                this.checkAudit(AuditType.CHANGEPERMISSIONDESCRIPTOR);
            }

            this.value.setInherit(inherit);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public boolean isHasChild() {
        if (!this.permission) {
            throw new StatusDisabilityException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        try {
            this.lock(LockType.READ);
            this.init();

            if (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                    && !this.value.getOwners().contains(processToken.getAccountID())
                    && this.denyPermission(PermissionType.READPERMISSIONDESCRIPTOR_ALLOW, null)) {
                throw new ConditionRefuseException();
            }

            return this.value.isHasChild();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public Set<UUID> getOwners() {
        if (!this.permission) {
            throw new StatusDisabilityException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        try {
            this.lock(LockType.READ);
            this.init();

            if (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                    && !this.value.getOwners().contains(processToken.getAccountID())
                    && this.denyPermission(PermissionType.READPERMISSIONDESCRIPTOR_ALLOW, null)) {
                throw new ConditionPermissionException();
            }

            if (this.audit) {
                this.checkAudit(AuditType.READPERMISSIONDESCRIPTOR);
            }

            return CollectionUtil.unmodifiable(this.value.getOwners());
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void setOwners(Set<UUID> owners) {
        if (ObjectUtil.isAnyNull(owners) || owners.isEmpty()) {
            throw new ConditionParametersException();
        }
        if (!this.permission) {
            throw new StatusDisabilityException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        try {
            this.lock(LockType.WRITE);
            this.init();

            if ((!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                    && !this.value.getOwners().contains(processToken.getAccountID())
                    && this.denyPermission(PermissionType.TAKEONWERSHIP_ALLOW, null))
                    || (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                    && !owners.contains(processToken.getAccountID()))) {
                throw new ConditionPermissionException();
            }

            if (this.audit) {
                this.checkAudit(AuditType.TAKEONWERSHIP);
            }

            this.value.getOwners().clear();
            this.value.getOwners().addAll(owners);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    private boolean denyPermission(long permission, PermissionQueryDefinition permissionQueryFunc) {
        if (!LogicalUtil.isAllSingleValue(permission) || LogicalUtil.isAnyExist(permission, PermissionType.FULLCONTROL_DENY)) {
            throw new ConditionParametersException();
        }
        if (!this.permission) {
            throw new StatusDisabilityException();
        }

        ProcessObject process = this.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if ((ObjectUtil.isAnyNull(permissionQueryFunc) || permissionQueryFunc.isPrivilege())
                && processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)) {
            return false;
        }

        List<SecurityDescriptorDefinition> securityDescriptors = new ArrayList<>();

        try {
            this.lock(LockType.READ);
            this.init();

            SecurityDescriptorObject securityDescriptor = this;
            do {
                if (securityDescriptor.permission) {
                    securityDescriptors.add(securityDescriptor.value);

                    if (!securityDescriptor.value.isInherit()) {
                        break;
                    }
                } else {
                    break;
                }

                securityDescriptor = securityDescriptor.parent;
            } while (securityDescriptor != null);
            Collections.reverse(securityDescriptors);
        } finally {
            this.lock(LockType.NONE);
        }

        Set<AccessControlDefinition> effectivePermissions = new HashSet<>();
        for (int i = 0; i < securityDescriptors.size(); i++) {
            for (AccessControlDefinition accessControl : securityDescriptors.get(i).getPermissions()) {
                if (LogicalUtil.isAllExist(accessControl.getScope(), AccessControlScopeType.THIS)) {
                    if (i == securityDescriptors.size() - 1) {
                        effectivePermissions.add(accessControl);
                    }
                }
                if (LogicalUtil.isAllExist(accessControl.getScope(), AccessControlScopeType.CHILD_HAS_CHILD)) {
                    if (i == securityDescriptors.size() - 2 && securityDescriptors.get(i + 1).isHasChild()) {
                        effectivePermissions.add(accessControl);
                    }
                }
                if (LogicalUtil.isAllExist(accessControl.getScope(), AccessControlScopeType.CHILD_HAS_NOT_CHILD)) {
                    if (i == securityDescriptors.size() - 2 && !securityDescriptors.get(i + 1).isHasChild()) {
                        effectivePermissions.add(accessControl);
                    }
                }
                if (LogicalUtil.isAllExist(accessControl.getScope(), AccessControlScopeType.HIERARCHICAL_HAS_CHILD)) {
                    if (i < securityDescriptors.size() - 1 && securityDescriptors.get(securityDescriptors.size() - 1).isHasChild()) {
                        effectivePermissions.add(accessControl);
                    }
                }
                if (LogicalUtil.isAllExist(accessControl.getScope(), AccessControlScopeType.HIERARCHICAL_HAS_NOT_CHILD)) {
                    if (i < securityDescriptors.size() - 1 && !securityDescriptors.get(securityDescriptors.size() - 1).isHasChild()) {
                        effectivePermissions.add(accessControl);
                    }
                }
            }
        }

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account = userManager.getCurrentAccount();
        UUID accountID = account.getID();
        Set<GroupObject> groups = account.getGroups();
        Set<UUID> groupIDs = new HashSet<>();
        for (GroupObject group : groups) {
            groupIDs.add(group.getID());
        }
        Set<UUID> roles = processToken.getRoles();

        boolean allow = false;

        for (AccessControlDefinition pair : effectivePermissions) {
            UserIDDefinition pairUserID = pair.getUserID();
            if (LogicalUtil.isAllExist(pairUserID.getType(), UserType.GROUP) && groupIDs.contains(pairUserID.getID())) {
                if (LogicalUtil.isAllExist(pair.getValue(), permission)) {
                    allow = true;
                }
                if (LogicalUtil.isAnyExist(pair.getValue(), permission << 1)) {
                    return true;
                }
            } else if (LogicalUtil.isAllExist(pairUserID.getType(), UserType.ACCOUNT) && accountID.equals(pairUserID.getID())) {
                if (LogicalUtil.isAllExist(pair.getValue(), permission)) {
                    allow = true;
                }
                if (LogicalUtil.isAnyExist(pair.getValue(), permission << 1)) {
                    return true;
                }
            } else if (LogicalUtil.isAllExist(pairUserID.getType(), UserType.ROLE) && roles.contains(pairUserID.getID())) {
                if (LogicalUtil.isAllExist(pair.getValue(), permission)
                        && (ObjectUtil.isAnyNull(permissionQueryFunc) || permissionQueryFunc.isRole())) {
                    allow = true;
                }
                if (LogicalUtil.isAnyExist(pair.getValue(), permission << 1)) {
                    return true;
                }
            } else if (LogicalUtil.isAllExist(pairUserID.getType(), UserType.PROCESS) && process.getID().equals(pairUserID.getID())) {
                if (LogicalUtil.isAllExist(pair.getValue(), permission)) {
                    allow = true;
                }
                if (LogicalUtil.isAnyExist(pair.getValue(), permission << 1)) {
                    return true;
                }
            } else if (LogicalUtil.isAllExist(pairUserID.getType(), UserType.PARENT_PROCESS)
                    && !ValueUtil.isAnyNullOrEmpty(process.getParentID()) && process.getParentID().equals(pairUserID.getID())) {
                if (LogicalUtil.isAllExist(pair.getValue(), permission)) {
                    allow = true;
                }
                if (LogicalUtil.isAnyExist(pair.getValue(), permission << 1)) {
                    return true;
                }
            }
            if (ObjectUtil.allNotNull(permissionQueryFunc) && ObjectUtil.allNotNull(permissionQueryFunc.getCustomDenyFunc()) &&
                    permissionQueryFunc.getCustomDenyFunc().test(pair.deepClone(), permission)) {
                return true;
            }
        }

        return !allow;
    }

    public void checkPermission(long permission, PermissionQueryDefinition permissionQueryFunc) {
        if (ObjectUtil.isAnyNull(permissionQueryFunc)) {
            throw new ConditionParametersException();
        }

        if (this.denyPermission(permission, permissionQueryFunc)) {
            throw new ConditionPermissionException();
        }
    }

    public void checkPermission(long permission) {
        if (this.denyPermission(permission, null)) {
            throw new ConditionPermissionException();
        }
    }

    public void setPermissions(Set<AccessControlDefinition> permissions) {
        if (ObjectUtil.isAnyNull(permissions)) {
            throw new ConditionParametersException();
        }
        if (!this.permission) {
            throw new StatusDisabilityException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            for (AccessControlDefinition permission : permissions) {
                if (!this.value.isHasChild() && LogicalUtil.isAnyExist(permission.getScope(),
                        LogicalUtil.or(AccessControlScopeType.CHILD_HAS_CHILD,
                                AccessControlScopeType.CHILD_HAS_NOT_CHILD,
                                AccessControlScopeType.HIERARCHICAL_HAS_CHILD,
                                AccessControlScopeType.HIERARCHICAL_HAS_NOT_CHILD))) {
                    throw new ConditionParametersException();
                }
            }

            ProcessTokenObject processToken = this.getCurrentProcessToken();

            if (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                    && !this.value.getOwners().contains(processToken.getAccountID())
                    && this.denyPermission(PermissionType.CHANGEPERMISSIONDESCRIPTOR_ALLOW, null)) {
                throw new ConditionPermissionException();
            }

            if (this.audit) {
                this.checkAudit(AuditType.CHANGEPERMISSIONDESCRIPTOR);
            }

            this.value.getPermissions().clear();
            this.value.getPermissions().addAll(permissions);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    private void writeAudit(Set<UserIDDefinition> userIDs, long value) {
        KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account = userManager.getCurrentAccount();
        String accountName = account.getName();

        try {
            this.lock(LockType.WRITE);
            this.init();

            List<IdentificationDefinition> identifications
                    = List.of(new IdentificationDefinition("Audits"), new IdentificationDefinition(accountName));

            InfoObject auditsInfo = objectManager.get(identifications);

            InfoObject auditInfo = auditsInfo.createChildAndOpen(kernelConfiguration.SECURITY_INSTANCE_AUDIT_ID,
                    new IdentificationDefinition(UUID.randomUUID()), InfoOpenAttributeType.OPEN_EXCLUSIVE);

            AuditContentObject auditContent = (AuditContentObject) auditInfo.getContent();
            auditContent.setUserIDs(userIDs);
            auditContent.setAudit(value);
            auditContent.setIdentifications(this.identifications);

            auditInfo.close();

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void checkAudit(long audit) {
        if (LogicalUtil.isAllExist(audit, AuditType.NULL)) {
            throw new ConditionParametersException();
        }
        if (!this.audit) {
            throw new StatusDisabilityException();
        }

        ProcessObject process = this.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        List<SecurityDescriptorDefinition> securityDescriptors = new ArrayList<>();

        try {
            this.lock(LockType.READ);
            this.init();

            SecurityDescriptorObject securityDescriptor = this;
            do {
                if (securityDescriptor.audit) {
                    securityDescriptors.add(securityDescriptor.value);
                } else {
                    break;
                }

                securityDescriptor = securityDescriptor.parent;
            } while (securityDescriptor != null);
            Collections.reverse(securityDescriptors);
        } finally {
            this.lock(LockType.NONE);
        }

        Set<AccessControlDefinition> effectiveAudits = new HashSet<>();
        for (int i = 0; i < securityDescriptors.size(); i++) {
            for (AccessControlDefinition pair : securityDescriptors.get(i).getAudits()) {
                if (LogicalUtil.isAllExist(AccessControlScopeType.THIS, pair.getScope())) {
                    if (i == securityDescriptors.size() - 1) {
                        effectiveAudits.add(pair);
                    }
                }
                if (LogicalUtil.isAllExist(AccessControlScopeType.CHILD_HAS_CHILD, pair.getScope())) {
                    if (i == securityDescriptors.size() - 2 && securityDescriptors.get(i + 1).isHasChild()) {
                        effectiveAudits.add(pair);
                    }
                }
                if (LogicalUtil.isAllExist(AccessControlScopeType.CHILD_HAS_NOT_CHILD, pair.getScope())) {
                    if (i == securityDescriptors.size() - 2 && !securityDescriptors.get(i + 1).isHasChild()) {
                        effectiveAudits.add(pair);
                    }
                }
                if (LogicalUtil.isAllExist(AccessControlScopeType.HIERARCHICAL_HAS_CHILD, pair.getScope())) {
                    if (i < securityDescriptors.size() - 1 && securityDescriptors.get(securityDescriptors.size() - 1).isHasChild()) {
                        effectiveAudits.add(pair);
                    }
                }
                if (LogicalUtil.isAllExist(AccessControlScopeType.HIERARCHICAL_HAS_NOT_CHILD, pair.getScope())) {
                    if (i < securityDescriptors.size() - 1 && !securityDescriptors.get(securityDescriptors.size() - 1).isHasChild()) {
                        effectiveAudits.add(pair);
                    }
                }
            }
        }

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account = userManager.getCurrentAccount();
        UUID accountID = account.getID();
        Set<GroupObject> groups = account.getGroups();
        Set<UUID> groupIDs = new HashSet<>();
        for (GroupObject group : groups) {
            groupIDs.add(group.getID());
        }
        Set<UUID> roles = processToken.getRoles();
        Set<UserIDDefinition> userIDs = new HashSet<>();

        for (AccessControlDefinition pair : effectiveAudits) {
            UserIDDefinition pairUserID = pair.getUserID();
            if (pairUserID.getType() == UserType.GROUP && groupIDs.contains(pairUserID.getID())) {
                if (LogicalUtil.isAllExist(audit, pair.getValue())) {
                    userIDs.add(pairUserID);
                }
            } else if (pairUserID.getType() == UserType.ACCOUNT && accountID.equals(pairUserID.getID())) {
                if (LogicalUtil.isAllExist(audit, pair.getValue())) {
                    userIDs.add(pairUserID);
                }
            } else if (pairUserID.getType() == UserType.ROLE && roles.contains(pairUserID.getID())) {
                if (LogicalUtil.isAllExist(audit, pair.getValue())) {
                    userIDs.add(pairUserID);
                }
            } else if (LogicalUtil.isAllExist(pairUserID.getType(), UserType.PROCESS) && process.getID().equals(pairUserID.getID())) {
                if (LogicalUtil.isAllExist(audit, pair.getValue())) {
                    userIDs.add(pairUserID);
                }
            } else if (LogicalUtil.isAllExist(pairUserID.getType(), UserType.PARENT_PROCESS)
                    && !ValueUtil.isAnyNullOrEmpty(process.getParentID()) && process.getParentID().equals(pairUserID.getID())) {
                if (LogicalUtil.isAllExist(audit, pair.getValue())) {
                    userIDs.add(pairUserID);
                }
            }
        }

        if (!userIDs.isEmpty()) {
            this.writeAudit(userIDs, audit);
        }
    }

    public void setAudits(Set<AccessControlDefinition> audits) {
        if (ObjectUtil.isAnyNull(audits)) {
            throw new ConditionParametersException();
        }
        if (!this.audit) {
            throw new StatusDisabilityException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        try {
            if (this.permission && !processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                    && !this.value.getOwners().contains(processToken.getAccountID())
                    && this.denyPermission(PermissionType.CHANGEPERMISSIONDESCRIPTOR_ALLOW, null)) {
                throw new ConditionAuditException();
            }

            this.lock(LockType.WRITE);
            this.init();

            this.value.getAudits().clear();
            this.value.getAudits().addAll(audits);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
