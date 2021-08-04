package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessPrototype;
import indi.sly.system.common.values.IdentificationDefinition;
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
public class SecurityDescriptorObject extends ABytesValueProcessPrototype<SecurityDescriptorDefinition> {
    public SecurityDescriptorObject() {
        this.identifications = new ArrayList<>();
        this.parents = new ArrayList<>();
    }

    @Override
    protected void read(byte[] source) {
        super.read(source);

        if (ObjectUtil.isAnyNull(this.value)) {
            this.value = new SecurityDescriptorDefinition();
        }
    }

    @Override
    protected byte[] write() {
        byte[] source = super.write();

        if (source.length > 4096) {
            throw new StatusInsufficientResourcesException();
        }

        return source;
    }

    private final List<IdentificationDefinition> identifications;
    private final List<SecurityDescriptorObject> parents;
    private boolean permission;
    private boolean audit;

    public void setIdentifications(List<IdentificationDefinition> identifications) {
        if (ObjectUtil.isAnyNull(identifications)) {
            throw new ConditionParametersException();
        }

        this.identifications.clear();
        this.identifications.addAll(identifications);
    }

    public void setParentSecurityDescriptor(SecurityDescriptorObject parentSecurityDescriptor) {
        if (ObjectUtil.isAnyNull(parentSecurityDescriptor)) {
            throw new ConditionParametersException();
        }

        this.parents.addAll(parentSecurityDescriptor.parents);
        this.parents.add(parentSecurityDescriptor);
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    public void setAudit(boolean audit) {
        this.audit = audit;
    }

    private ProcessTokenObject getCurrentProcessToken() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();

        return process.getToken();
    }

    public List<SecurityDescriptorSummaryDefinition> getSummary() {
        if (!this.permission && !this.audit) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (this.permission) {
            if (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                    && !this.value.getOwners().contains(processToken.getAccountID())
                    && !this.allowPermission(PermissionType.READPERMISSIONDESCRIPTOR_ALLOW)) {
                throw new ConditionPermissionsException();
            }
        }

        if (this.audit) {
            this.checkAudit(AuditType.READPERMISSIONDESCRIPTOR);
        }

        this.init();

        List<SecurityDescriptorSummaryDefinition> securityDescriptorSummaries = new ArrayList<>();

        for (SecurityDescriptorObject pair : this.parents) {
            pair.init();

            SecurityDescriptorSummaryDefinition securityDescriptorSummary = new SecurityDescriptorSummaryDefinition();
            securityDescriptorSummary.getIdentifications().addAll(pair.identifications);

            if (pair.permission) {
                securityDescriptorSummary.setPermission(true);
                securityDescriptorSummary.setInherit(pair.value.isInherit());
                securityDescriptorSummary.getPermissions().addAll(pair.value.getPermissions());
            } else {
                securityDescriptorSummary.setPermission(false);
            }

            if (pair.audit) {
                securityDescriptorSummary.setAudit(true);
                securityDescriptorSummary.getAudits().addAll(pair.value.getAudits());
            } else {
                securityDescriptorSummary.setAudit(false);
            }

            securityDescriptorSummaries.add(securityDescriptorSummary);
        }

        SecurityDescriptorSummaryDefinition securityDescriptorSummary = new SecurityDescriptorSummaryDefinition();
        securityDescriptorSummary.getIdentifications().addAll(this.identifications);

        if (this.permission) {
            securityDescriptorSummary.setPermission(true);
            securityDescriptorSummary.setInherit(this.value.isInherit());
            securityDescriptorSummary.getPermissions().addAll(this.value.getPermissions());

        } else {
            securityDescriptorSummary.setPermission(false);
        }
        if (this.audit) {
            securityDescriptorSummary.setAudit(true);
            securityDescriptorSummary.getAudits().addAll(this.value.getAudits());
        } else {
            securityDescriptorSummary.setAudit(false);
        }

        securityDescriptorSummaries.add(securityDescriptorSummary);

        return securityDescriptorSummaries;
    }

    public boolean isInherit() {
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())
                && !this.allowPermission(PermissionType.READPERMISSIONDESCRIPTOR_ALLOW)) {
            throw new ConditionPermissionsException();
        }

        if (this.audit) {
            this.checkAudit(AuditType.READPERMISSIONDESCRIPTOR);
        }

        this.init();

        return this.value.isInherit();
    }

    public void setInherit(boolean inherit) {
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())
                && !this.allowPermission(PermissionType.CHANGEPERMISSIONDESCRIPTOR_ALLOW)) {
            throw new ConditionPermissionsException();
        }

        if (this.audit) {
            this.checkAudit(AuditType.CHANGEPERMISSIONDESCRIPTOR);
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setInherit(inherit);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public boolean isHasChild() {
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())
                && !this.allowPermission(PermissionType.READPERMISSIONDESCRIPTOR_ALLOW)) {
            throw new ConditionPermissionsException();
        }

        this.init();

        return this.value.isHasChild();
    }

    public Set<UUID> getOwners() {
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())
                && !this.allowPermission(PermissionType.READPERMISSIONDESCRIPTOR_ALLOW)) {
            throw new ConditionPermissionsException();
        }

        if (this.audit) {
            this.checkAudit(AuditType.READPERMISSIONDESCRIPTOR);
        }

        this.init();

        return Collections.unmodifiableSet(this.value.getOwners());
    }

    public void setOwners(Set<UUID> owners) {
        if (ObjectUtil.isAnyNull(owners) || owners.isEmpty()) {
            throw new ConditionParametersException();
        }
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if ((!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())
                && !this.allowPermission(PermissionType.TAKEONWERSHIP_ALLOW))
                || (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                && !owners.contains(processToken.getAccountID()))) {
            throw new ConditionPermissionsException();
        }

        if (this.audit) {
            this.checkAudit(AuditType.TAKEONWERSHIP);
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.getOwners().clear();
        this.value.getOwners().addAll(owners);

        this.fresh();
        this.lock(LockType.NONE);
    }

    private boolean allowPermission(long permission) {
        if (permission == PermissionType.NULL || LogicalUtil.isAnyExist(permission,
                PermissionType.FULLCONTROL_DENY)) {
            throw new ConditionParametersException();
        }
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)) {
            return true;
        }

        List<SecurityDescriptorDefinition> securityDescriptors = new ArrayList<>();
        for (SecurityDescriptorObject pair : this.parents) {
            pair.init();

            if (pair.permission) {
                securityDescriptors.add(pair.value);
            } else {
                securityDescriptors.clear();
            }
        }

        this.init();

        securityDescriptors.add(this.value);

        Set<AccessControlDefinition> effectivePermissions = new HashSet<>();
        for (int i = 0; i < securityDescriptors.size(); i++) {
            if (!securityDescriptors.get(i).isInherit()) {
                effectivePermissions.clear();
            }

            for (AccessControlDefinition pair : securityDescriptors.get(i).getPermissions()) {
                if (LogicalUtil.isAllExist(AccessControlScopeType.THIS, pair.getScope())) {
                    if (i == securityDescriptors.size() - 1) {
                        effectivePermissions.add(pair);
                    }
                }
                if (LogicalUtil.isAllExist(AccessControlScopeType.CHILD_HAS_CHILD, pair.getScope())) {
                    if (i == securityDescriptors.size() - 2 && securityDescriptors.get(i + 1).isHasChild()) {
                        effectivePermissions.add(pair);
                    }
                }
                if (LogicalUtil.isAllExist(AccessControlScopeType.CHILD_HAS_NOT_CHILD, pair.getScope())) {
                    if (i == securityDescriptors.size() - 2 && !securityDescriptors.get(i + 1).isHasChild()) {
                        effectivePermissions.add(pair);
                    }
                }
                if (LogicalUtil.isAllExist(AccessControlScopeType.HIERARCHICAL_HAS_CHILD, pair.getScope())) {
                    if (i < securityDescriptors.size() - 1 && securityDescriptors.get(securityDescriptors.size() - 1).isHasChild()) {
                        effectivePermissions.add(pair);
                    }
                }
                if (LogicalUtil.isAllExist(AccessControlScopeType.HIERARCHICAL_HAS_NOT_CHILD, pair.getScope())) {
                    if (i < securityDescriptors.size() - 1 && !securityDescriptors.get(securityDescriptors.size() - 1).isHasChild()) {
                        effectivePermissions.add(pair);
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
            if (pairUserID.getType() == UserType.GROUP && groupIDs.contains(pairUserID.getID())) {
                if (LogicalUtil.isAllExist(permission, pair.getValue())) {
                    allow = true;
                }
                if (LogicalUtil.isAnyExist(pair.getValue(), permission << 1)) {
                    return false;
                }
            } else if (pairUserID.getType() == UserType.ACCOUNT && accountID.equals(pairUserID.getID())) {
                if (LogicalUtil.isAllExist(permission, pair.getValue())) {
                    allow = true;
                }
                if (LogicalUtil.isAnyExist(pair.getValue(), permission << 1)) {
                    return false;
                }
            } else if (pairUserID.getType() == UserType.ROLE && roles.contains(pairUserID.getID())) {
                if (LogicalUtil.isAllExist(permission, pair.getValue())) {
                    allow = true;
                }
                if (LogicalUtil.isAnyExist(pair.getValue(), permission << 1)) {
                    return false;
                }
            }
        }

        return allow;
    }

    public void checkPermission(long permission) {
        if (!this.allowPermission(permission)) {
            throw new ConditionPermissionsException();
        }
    }

    public void setPermissions(Set<AccessControlDefinition> permissions) {
        if (ObjectUtil.isAnyNull(permissions)) {
            throw new ConditionParametersException();
        }
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

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
                && !this.allowPermission(PermissionType.CHANGEPERMISSIONDESCRIPTOR_ALLOW)) {
            throw new ConditionPermissionsException();
        }

        if (this.audit) {
            this.checkAudit(AuditType.CHANGEPERMISSIONDESCRIPTOR);
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.getPermissions().clear();
        this.value.getPermissions().addAll(permissions);

        this.fresh();
        this.lock(LockType.NONE);
    }

    private void writeAudit(Set<UserIDDefinition> userIDs, long value) {
        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account = userManager.getCurrentAccount();
        String accountName = account.getName();

        try {
            this.lock(LockType.WRITE);
            this.init();

            UUID typeID = this.factoryManager.getKernelSpace().getConfiguration().SECURITY_INSTANCE_AUDIT_ID;

            InfoObject audits = objectManager.get(List.of(new IdentificationDefinition("Audits")));
            InfoObject audit = null;

            try {
                audits = audits.getChild(new IdentificationDefinition(accountName));
                audit = audits.createChildAndOpen(typeID, new IdentificationDefinition(UUID.randomUUID()),
                        InfoOpenAttributeType.OPEN_EXCLUSIVE);
            } catch (AKernelException ignore) {
                return;
            }

            AuditContentObject auditContent = (AuditContentObject) audit.getContent();
            auditContent.setUserIDs(userIDs);
            auditContent.setAudit(value);
            auditContent.setIdentifications(this.identifications);
            audit.close();

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void checkAudit(long audit) {
        if (audit == AuditType.NULL) {
            throw new ConditionParametersException();
        }
        if (!this.audit) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        List<SecurityDescriptorDefinition> securityDescriptors = new ArrayList<>();
        for (SecurityDescriptorObject pair : this.parents) {
            pair.init();

            if (pair.audit) {
                securityDescriptors.add(pair.value);
            } else {
                securityDescriptors.clear();
            }
        }

        this.init();

        securityDescriptors.add(this.value);

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
            }
        }

        this.writeAudit(userIDs, audit);
    }

    public void setAudits(Set<AccessControlDefinition> audits) {
        if (ObjectUtil.isAnyNull(audits)) {
            throw new ConditionParametersException();
        }
        if (!this.audit) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (this.permission && !processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())
                && !this.allowPermission(PermissionType.CHANGEPERMISSIONDESCRIPTOR_ALLOW)) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.getAudits().clear();
        this.value.getAudits().addAll(audits);

        this.fresh();
        this.lock(LockType.NONE);
    }
}
