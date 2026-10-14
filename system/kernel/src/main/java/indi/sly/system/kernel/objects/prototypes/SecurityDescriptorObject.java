package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentifierRecord;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.core.environment.containers.KernelConfiguration;
import indi.sly.system.kernel.core.prototypes.AChildCacheableObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.mediators.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessSessionObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.instances.prototypes.AuditContentObject;
import indi.sly.system.kernel.security.prototypes.AccountObject;
import indi.sly.system.kernel.security.prototypes.GroupObject;
import indi.sly.system.kernel.security.values.*;
import jakarta.inject.Named;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SecurityDescriptorObject extends AChildCacheableObject<SecurityDescriptorCacheEntity, InfoObject> {
    @SuppressWarnings("NullAway.Init")
    protected InfoProcessorMediator processorMediator;

    private InfoEntity getSelf(long lock) {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getInfo().getInfoId())) {
            throw new ConditionContextException();
        }

        return this.processorMediator.getSelf().apply(this.cache.getInfo(), lock);
    }

    public void initSelf(long lock) {
        this.getSelf(lock);
    }

    private ProcessObject getCurrentProcess() {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);

        return processManager.getCurrent();
    }

    private ProcessTokenObject getCurrentProcessToken() {
        ProcessObject process = this.getCurrentProcess();

        return process.getToken();
    }

    public List<SecurityDescriptorSummaryRecord> getSummary() {
        if (!this.cache.isPermission() && !this.cache.isAudit()) {
            throw new StatusDisabilityException();
        }

        InfoEntity info = this.getSelf(LockType.READ);

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        SecurityDescriptorEntity securityDescriptor = info.getSecurityDescriptor();

        if (this.cache.isPermission()) {
            if (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                    && !securityDescriptor.getOwners().contains(processToken.getAccountId())
                    && this.denyPermission(PermissionType.READPERMISSIONDESCRIPTOR_ALLOW, null)) {
                throw new ConditionPermissionException();
            }
        }

        if (this.cache.isAudit()) {
            this.checkAudit(AuditType.READPERMISSIONDESCRIPTOR);
        }

        List<SecurityDescriptorSummaryRecord> securityDescriptorSummaries = new ArrayList<>();

        SecurityDescriptorSummaryRecord securityDescriptorSummary = new SecurityDescriptorSummaryRecord(
                this.cache.getInfo().getPath(),
                this.cache.isPermission() && securityDescriptor.isInherit(),
                this.cache.isPermission(),
                this.cache.isAudit(),
                this.cache.isPermission() ? new HashSet<>(securityDescriptor.getPermissions()) : new HashSet<>(),
                this.cache.isAudit() ? new HashSet<>(securityDescriptor.getAudits()) : new HashSet<>()
        );
        securityDescriptorSummaries.add(securityDescriptorSummary);

        InfoObject parentInfo = this.base.getParent();

        SecurityDescriptorObject parentSecurityDescriptor;
        InfoEntity parentInfoSelf;

        KernelConfiguration kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();
        int MAX_DEPTH = kernelConfiguration.OBJECTS_INFO_PATH_MAX_DEPTH;
        while (ObjectUtil.allNotNull(parentInfo)) {
            if (MAX_DEPTH-- < 0) {
                throw new StatusOverflowException();
            }

            parentSecurityDescriptor = parentInfo.getSecurityDescriptor();

            if (parentSecurityDescriptor.cache.isPermission() || parentSecurityDescriptor.cache.isAudit()) {
                parentInfoSelf = parentSecurityDescriptor.getSelf(LockType.READ);
                securityDescriptor = parentInfoSelf.getSecurityDescriptor();

                securityDescriptorSummary = new SecurityDescriptorSummaryRecord(
                        parentSecurityDescriptor.cache.getInfo().getPath(),
                        securityDescriptor.isInherit(),
                        parentSecurityDescriptor.cache.isPermission(),
                        parentSecurityDescriptor.cache.isAudit(),
                        parentSecurityDescriptor.cache.isPermission() ? new HashSet<>(securityDescriptor.getPermissions()) : new HashSet<>(),
                        parentSecurityDescriptor.cache.isAudit() ? new HashSet<>(securityDescriptor.getAudits()) : new HashSet<>()
                );
                securityDescriptorSummaries.add(securityDescriptorSummary);
            }

            parentInfo = parentInfo.getParent();
        }

        Collections.reverse(securityDescriptorSummaries);

        return securityDescriptorSummaries;
    }

    public boolean isInherit() {
        if (!this.cache.isPermission()) {
            throw new StatusDisabilityException();
        }

        InfoEntity info = this.getSelf(LockType.READ);

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        SecurityDescriptorEntity securityDescriptor = info.getSecurityDescriptor();

        if (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                && !securityDescriptor.getOwners().contains(processToken.getAccountId())
                && this.denyPermission(PermissionType.READPERMISSIONDESCRIPTOR_ALLOW, null)) {
            throw new ConditionPermissionException();
        }

        if (this.cache.isAudit()) {
            this.checkAudit(AuditType.READPERMISSIONDESCRIPTOR);
        }

        return securityDescriptor.isInherit();
    }

    public void setInherit(boolean inherit) {
        if (!this.cache.isPermission()) {
            throw new StatusDisabilityException();
        }

        InfoEntity info = this.getSelf(LockType.WRITE);

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        SecurityDescriptorEntity securityDescriptor = info.getSecurityDescriptor();

        if (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                && !securityDescriptor.getOwners().contains(processToken.getAccountId())
                && this.denyPermission(PermissionType.CHANGEPERMISSIONDESCRIPTOR_ALLOW, null)) {
            throw new ConditionPermissionException();
        }

        if (this.cache.isAudit()) {
            this.checkAudit(AuditType.CHANGEPERMISSIONDESCRIPTOR);
        }

        securityDescriptor.setInherit(inherit);

        info.setSecurityDescriptor(securityDescriptor);
    }

    public Set<UUID> getOwners() {
        if (!this.cache.isPermission()) {
            throw new StatusDisabilityException();
        }

        InfoEntity info = this.getSelf(LockType.READ);

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        SecurityDescriptorEntity securityDescriptor = info.getSecurityDescriptor();

        if (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                && !securityDescriptor.getOwners().contains(processToken.getAccountId())
                && this.denyPermission(PermissionType.READPERMISSIONDESCRIPTOR_ALLOW, null)) {
            throw new ConditionPermissionException();
        }

        if (this.cache.isAudit()) {
            this.checkAudit(AuditType.READPERMISSIONDESCRIPTOR);
        }

        return CollectionUtil.unmodifiable(securityDescriptor.getOwners());
    }

    public void setOwners(Set<UUID> owners) {
        if (ObjectUtil.isAnyNull(owners) || owners.isEmpty()) {
            throw new ConditionParametersException();
        }
        if (!this.cache.isPermission()) {
            throw new StatusDisabilityException();
        }

        InfoEntity info = this.getSelf(LockType.WRITE);

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        SecurityDescriptorEntity securityDescriptor = info.getSecurityDescriptor();

        if ((!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                && !securityDescriptor.getOwners().contains(processToken.getAccountId())
                && this.denyPermission(PermissionType.TAKEONWERSHIP_ALLOW, null))
                || (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                && !owners.contains(processToken.getAccountId()))) {
            throw new ConditionPermissionException();
        }
        if (!securityDescriptor.isCanChangeOwner()) {
            throw new ConditionRefuseException();
        }

        if (this.cache.isAudit()) {
            this.checkAudit(AuditType.TAKEONWERSHIP);
        }

        securityDescriptor.getOwners().clear();
        securityDescriptor.getOwners().addAll(owners);

        info.setSecurityDescriptor(securityDescriptor);
    }

    private boolean denyPermission(long permission, @Nullable PermissionQueryRecord permissionQueryFunc) {
        if (!LogicalUtil.isAllSingleValue(permission) || LogicalUtil.isAnyExist(permission, PermissionType.FULLCONTROL_ALLOW << 1)) {
            throw new ConditionParametersException();
        }
        if (!this.cache.isPermission()) {
            throw new StatusDisabilityException();
        }

        List<SecurityDescriptorEntity> securityDescriptors = new ArrayList<>();

        InfoEntity info = this.getSelf(LockType.READ);

        ProcessObject process = this.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();
        ProcessSessionObject processSession = process.getSession();

        if ((ObjectUtil.isAnyNull(permissionQueryFunc) || permissionQueryFunc.privilege())
                && processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)) {
            return false;
        }

        SecurityDescriptorEntity securityDescriptor = info.getSecurityDescriptor();
        securityDescriptors.add(securityDescriptor);

        InfoObject parentInfo = this.base.getParent();

        if (securityDescriptor.isInherit()) {
            SecurityDescriptorObject parentSecurityDescriptor;
            InfoEntity parentInfoSelf;

            KernelConfiguration kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();
            int MAX_DEPTH = kernelConfiguration.OBJECTS_INFO_PATH_MAX_DEPTH;
            while (ObjectUtil.allNotNull(parentInfo)) {
                if (MAX_DEPTH-- < 0) {
                    throw new StatusOverflowException();
                }

                parentSecurityDescriptor = parentInfo.getSecurityDescriptor();

                if (parentSecurityDescriptor.cache.isPermission()) {
                    parentInfoSelf = parentSecurityDescriptor.getSelf(LockType.READ);

                    securityDescriptor = parentInfoSelf.getSecurityDescriptor();
                    securityDescriptors.add(securityDescriptor);

                    if (!securityDescriptor.isInherit()) {
                        break;
                    }
                } else {
                    break;
                }

                parentInfo = parentInfo.getParent();
            }
        }

        Collections.reverse(securityDescriptors);

        Set<AccessControlRecord> effectivePermissions = new HashSet<>();
        boolean securityDescriptorHasChild;
        for (int i = 0; i < securityDescriptors.size(); i++) {
            for (AccessControlRecord accessControl : securityDescriptors.get(i).getPermissions()) {
                if (LogicalUtil.isAllExist(accessControl.scope(), AccessControlScopeType.THIS)) {
                    if (i == securityDescriptors.size() - 1) {
                        effectivePermissions.add(accessControl);
                    }
                }
                securityDescriptorHasChild = securityDescriptors.getLast().isHasChild();
                if (LogicalUtil.isAllExist(accessControl.scope(), AccessControlScopeType.CHILD_HAS_CHILD)) {
                    if (i == securityDescriptors.size() - 2 && securityDescriptorHasChild) {
                        effectivePermissions.add(accessControl);
                    }
                }
                if (LogicalUtil.isAllExist(accessControl.scope(), AccessControlScopeType.CHILD_HAS_NOT_CHILD)) {
                    if (i == securityDescriptors.size() - 2 && !securityDescriptorHasChild) {
                        effectivePermissions.add(accessControl);
                    }
                }
                if (LogicalUtil.isAllExist(accessControl.scope(), AccessControlScopeType.HIERARCHICAL_HAS_CHILD)) {
                    if (i < securityDescriptors.size() - 1 && securityDescriptorHasChild) {
                        effectivePermissions.add(accessControl);
                    }
                }
                if (LogicalUtil.isAllExist(accessControl.scope(), AccessControlScopeType.HIERARCHICAL_HAS_NOT_CHILD)) {
                    if (i < securityDescriptors.size() - 1 && !securityDescriptorHasChild) {
                        effectivePermissions.add(accessControl);
                    }
                }
            }
        }

        UserManager userManager = this.coreManager.getManager(UserManager.class);

        AccountObject account = userManager.getCurrentAccount();
        UUID accountId = account.getId();
        Set<GroupObject> groups = account.getGroups();
        Set<UUID> groupIds = new HashSet<>();
        for (GroupObject group : groups) {
            groupIds.add(group.getId());
        }
        Set<UUID> roles = processToken.getRoles();

        boolean allow = false;

        for (AccessControlRecord pair : effectivePermissions) {
            UserIdRecord pairUserId = pair.userId();
            if (LogicalUtil.isAnyEqual(pairUserId.type(), UserType.GROUP) && groupIds.contains(pairUserId.id())) {
                if (LogicalUtil.isAllExist(pair.value(), permission)) {
                    allow = true;
                }
                if (LogicalUtil.isAnyExist(pair.value(), permission << 1)) {
                    return true;
                }
            } else if (LogicalUtil.isAnyEqual(pairUserId.type(), UserType.ACCOUNT) && accountId.equals(pairUserId.id())) {
                if (LogicalUtil.isAllExist(pair.value(), permission)) {
                    allow = true;
                }
                if (LogicalUtil.isAnyExist(pair.value(), permission << 1)) {
                    return true;
                }
            } else if (LogicalUtil.isAnyEqual(pairUserId.type(), UserType.ROLE) && roles.contains(pairUserId.id())) {
                if (LogicalUtil.isAllExist(pair.value(), permission)
                        && (ObjectUtil.isAnyNull(permissionQueryFunc) || permissionQueryFunc.role())) {
                    allow = true;
                }
                if (LogicalUtil.isAnyExist(pair.value(), permission << 1)) {
                    return true;
                }
            } else if (LogicalUtil.isAnyEqual(pairUserId.type(), UserType.PROCESS) && process.getId().equals(pairUserId.id())) {
                if (LogicalUtil.isAllExist(pair.value(), permission)) {
                    allow = true;
                }
                if (LogicalUtil.isAnyExist(pair.value(), permission << 1)) {
                    return true;
                }
            } else if (LogicalUtil.isAnyEqual(pairUserId.type(), UserType.PARENT_PROCESS)
                    && !ValueUtil.isAnyNullOrEmpty(process.getParentId()) && process.getParentId().equals(pairUserId.id())) {
                if (LogicalUtil.isAllExist(pair.value(), permission)) {
                    allow = true;
                }
                if (LogicalUtil.isAnyExist(pair.value(), permission << 1)) {
                    return true;
                }
            } else if (LogicalUtil.isAnyEqual(pairUserId.type(), UserType.SESSION)
                    && !ValueUtil.isAnyNullOrEmpty(processSession.getId()) && processSession.getId().equals(pairUserId.id())) {
                if (LogicalUtil.isAllExist(pair.value(), permission)) {
                    allow = true;
                }
                if (LogicalUtil.isAnyExist(pair.value(), permission << 1)) {
                    return true;
                }
            }
            if (ObjectUtil.allNotNull(permissionQueryFunc) && ObjectUtil.allNotNull(permissionQueryFunc.customDenyFunc()) &&
                    permissionQueryFunc.customDenyFunc().test(pair, permission)) {
                return true;
            }
        }

        return !allow;
    }

    public void checkPermission(long permission, @Nullable  PermissionQueryRecord permissionQueryFunc) {
        if (this.denyPermission(permission, permissionQueryFunc)) {
            throw new ConditionPermissionException();
        }
    }

    public void checkPermission(long permission) {
        this.checkPermission(permission, null);
    }

    public void setPermissions(Set<AccessControlRecord> permissions) {
        if (ObjectUtil.isAnyNull(permissions)) {
            throw new ConditionParametersException();
        }
        if (!this.cache.isPermission()) {
            throw new StatusDisabilityException();
        }

        InfoEntity info = this.getSelf(LockType.WRITE);

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        SecurityDescriptorEntity securityDescriptor = info.getSecurityDescriptor();

        for (AccessControlRecord permission : permissions) {
            if (!securityDescriptor.isHasChild() && LogicalUtil.isAnyExist(permission.scope(),
                    LogicalUtil.or(AccessControlScopeType.CHILD_HAS_CHILD,
                            AccessControlScopeType.CHILD_HAS_NOT_CHILD,
                            AccessControlScopeType.HIERARCHICAL_HAS_CHILD,
                            AccessControlScopeType.HIERARCHICAL_HAS_NOT_CHILD))) {
                throw new ConditionParametersException();
            }
        }

        if (!processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                && !securityDescriptor.getOwners().contains(processToken.getAccountId())
                && this.denyPermission(PermissionType.CHANGEPERMISSIONDESCRIPTOR_ALLOW, null)) {
            throw new ConditionPermissionException();
        }

        if (this.cache.isAudit()) {
            this.checkAudit(AuditType.CHANGEPERMISSIONDESCRIPTOR);
        }

        securityDescriptor.getPermissions().clear();
        securityDescriptor.getPermissions().addAll(permissions);

        info.setSecurityDescriptor(securityDescriptor);
    }

    private void writeAudit(Set<UserIdRecord> userIds, long value) {
        KernelConfiguration kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
        UserManager userManager = this.coreManager.getManager(UserManager.class);

        AccountObject account = userManager.getCurrentAccount();

        try {
            InfoObject auditsInfo = objectManager.get(new PathRecord(List.of(new IdentifierRecord("Audits"), new IdentifierRecord(account.getName()))));

            InfoObject auditInfo = auditsInfo.createChild(kernelConfiguration.SECURITY_INSTANCE_AUDIT_ID,
                    new IdentifierRecord(UUID.randomUUID()));
            auditInfo.open(InfoOpenAttributeType.OPEN_EXCLUSIVE);

            AuditContentObject auditContent = (AuditContentObject) auditInfo.getContent();
            auditContent.setUserIds(userIds);
            auditContent.setAudit(value);
            auditContent.setPath(this.cache.getInfo().getPath());

            auditInfo.close();
        } catch (ASystemException ignored) {
        }
    }

    public void checkAudit(long audit) {
        if (!LogicalUtil.isAllSingleValue(audit)) {
            throw new ConditionParametersException();
        }
        if (!this.cache.isAudit()) {
            throw new StatusDisabilityException();
        }

        List<SecurityDescriptorEntity> securityDescriptors = new ArrayList<>();

        InfoEntity info = this.getSelf(LockType.READ);

        ProcessObject process = this.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();
        ProcessSessionObject processSession = process.getSession();

        SecurityDescriptorEntity securityDescriptor = info.getSecurityDescriptor();
        securityDescriptors.add(securityDescriptor);

        InfoObject parentInfo = this.base.getParent();

        SecurityDescriptorObject parentSecurityDescriptor;
        InfoEntity parentInfoSelf;

        KernelConfiguration kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();
        int MAX_DEPTH = kernelConfiguration.OBJECTS_INFO_PATH_MAX_DEPTH;
        while (ObjectUtil.allNotNull(parentInfo)) {
            if (MAX_DEPTH-- < 0) {
                throw new StatusOverflowException();
            }

            parentSecurityDescriptor = parentInfo.getSecurityDescriptor();

            if (parentSecurityDescriptor.cache.isAudit()) {
                parentInfoSelf = parentSecurityDescriptor.getSelf(LockType.READ);

                securityDescriptor = parentInfoSelf.getSecurityDescriptor();
                securityDescriptors.add(securityDescriptor);
            } else {
                break;
            }

            parentInfo = parentInfo.getParent();
        }

        Collections.reverse(securityDescriptors);

        Set<AccessControlRecord> effectiveAudits = new HashSet<>();
        boolean securityDescriptorHasChild;
        for (int i = 0; i < securityDescriptors.size(); i++) {
            for (AccessControlRecord pair : securityDescriptors.get(i).getAudits()) {
                if (LogicalUtil.isAllExist(pair.scope(), AccessControlScopeType.THIS)) {
                    if (i == securityDescriptors.size() - 1) {
                        effectiveAudits.add(pair);
                    }
                }
                securityDescriptorHasChild = securityDescriptors.getLast().isHasChild();
                if (LogicalUtil.isAllExist(pair.scope(), AccessControlScopeType.CHILD_HAS_CHILD)) {
                    if (i == securityDescriptors.size() - 2 && securityDescriptorHasChild) {
                        effectiveAudits.add(pair);
                    }
                }
                if (LogicalUtil.isAllExist(pair.scope(), AccessControlScopeType.CHILD_HAS_NOT_CHILD)) {
                    if (i == securityDescriptors.size() - 2 && !securityDescriptorHasChild) {
                        effectiveAudits.add(pair);
                    }
                }
                if (LogicalUtil.isAllExist(pair.scope(), AccessControlScopeType.HIERARCHICAL_HAS_CHILD)) {
                    if (i < securityDescriptors.size() - 1 && securityDescriptorHasChild) {
                        effectiveAudits.add(pair);
                    }
                }
                if (LogicalUtil.isAllExist(pair.scope(), AccessControlScopeType.HIERARCHICAL_HAS_NOT_CHILD)) {
                    if (i < securityDescriptors.size() - 1 && !securityDescriptorHasChild) {
                        effectiveAudits.add(pair);
                    }
                }
            }
        }

        UserManager userManager = this.coreManager.getManager(UserManager.class);

        AccountObject account = userManager.getCurrentAccount();
        UUID accountId = account.getId();
        Set<GroupObject> groups = account.getGroups();
        Set<UUID> groupIds = new HashSet<>();
        for (GroupObject group : groups) {
            groupIds.add(group.getId());
        }
        Set<UUID> roles = processToken.getRoles();
        Set<UserIdRecord> userIds = new HashSet<>();

        for (AccessControlRecord pair : effectiveAudits) {
            UserIdRecord pairUserId = pair.userId();
            if (LogicalUtil.isAnyEqual(pairUserId.type(), UserType.GROUP) && groupIds.contains(pairUserId.id())) {
                if (LogicalUtil.isAllExist(audit, pair.value())) {
                    userIds.add(pairUserId);
                }
            } else if (LogicalUtil.isAnyEqual(pairUserId.type(), UserType.ACCOUNT) && accountId.equals(pairUserId.id())) {
                if (LogicalUtil.isAllExist(audit, pair.value())) {
                    userIds.add(pairUserId);
                }
            } else if (LogicalUtil.isAnyEqual(pairUserId.type(), UserType.ROLE) && roles.contains(pairUserId.id())) {
                if (LogicalUtil.isAllExist(audit, pair.value())) {
                    userIds.add(pairUserId);
                }
            } else if (LogicalUtil.isAnyEqual(pairUserId.type(), UserType.PROCESS) && process.getId().equals(pairUserId.id())) {
                if (LogicalUtil.isAllExist(audit, pair.value())) {
                    userIds.add(pairUserId);
                }
            } else if (LogicalUtil.isAnyEqual(pairUserId.type(), UserType.PARENT_PROCESS)
                    && !ValueUtil.isAnyNullOrEmpty(process.getParentId()) && process.getParentId().equals(pairUserId.id())) {
                if (LogicalUtil.isAllExist(audit, pair.value())) {
                    userIds.add(pairUserId);
                }
            } else if (LogicalUtil.isAnyEqual(pairUserId.type(), UserType.SESSION)
                    && !ValueUtil.isAnyNullOrEmpty(processSession.getId()) && processSession.getId().equals(pairUserId.id())) {
                if (LogicalUtil.isAllExist(audit, pair.value())) {
                    userIds.add(pairUserId);
                }
            }
        }

        if (!userIds.isEmpty()) {
            this.writeAudit(userIds, audit);
        }
    }

    public void setAudits(Set<AccessControlRecord> audits) {
        if (ObjectUtil.isAnyNull(audits)) {
            throw new ConditionParametersException();
        }
        if (!this.cache.isAudit()) {
            throw new StatusDisabilityException();
        }

        InfoEntity info = this.getSelf(LockType.WRITE);

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        SecurityDescriptorEntity securityDescriptor = info.getSecurityDescriptor();

        if (this.cache.isPermission() && !processToken.isPrivileges(PrivilegeType.OBJECTS_ACCESS_INFOOBJECTS)
                && !securityDescriptor.getOwners().contains(processToken.getAccountId())
                && this.denyPermission(PermissionType.CHANGEPERMISSIONDESCRIPTOR_ALLOW, null)) {
            throw new ConditionAuditException();
        }

        securityDescriptor.getAudits().clear();
        securityDescriptor.getAudits().addAll(audits);

        info.setSecurityDescriptor(securityDescriptor);
    }
}