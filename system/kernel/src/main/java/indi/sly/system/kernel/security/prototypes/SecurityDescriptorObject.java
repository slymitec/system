package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.common.exceptions.StatusInsufficientResourcesException;
import indi.sly.system.common.exceptions.StatusNotSupportedException;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.LogicalUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.prototypes.ABytesProcessObject;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.SecurityTokenManager;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SecurityDescriptorObject extends ABytesProcessObject {
    public SecurityDescriptorObject() {
        this.parents = new ArrayList<>();
        this.identifications = new ArrayList<>();
    }

    @Override
    protected void read(byte[] source) {
        if (ObjectUtils.isAnyNull(source)) {
            this.securityDescriptor = new SecurityDescriptorDefinition();
            this.securityDescriptor.setInherit(true);
        } else {
            this.securityDescriptor = ObjectUtils.transferFromByteArray(source);
        }
    }

    @Override
    protected byte[] write() {
        byte[] source = ObjectUtils.transferToByteArray(this.securityDescriptor);

        if (source.length > 4096) {
            throw new StatusInsufficientResourcesException();
        }

        return source;
    }

    private final List<Identification> identifications;
    private final List<SecurityDescriptorObject> parents;
    private SecurityDescriptorDefinition securityDescriptor;
    private boolean permission;
    private boolean audit;

    public void setParentSecurityDescriptor(SecurityDescriptorObject parentSecurityDescriptor) {
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

        ProcessObject process = processManager.getCurrentProcess();

        return process.getToken();
    }

    private boolean isAccessControlType(long accessControlType) {
        if (accessControlType == AccessControlTypes.NULL || LogicalUtils.isAnyExist(accessControlType, AccessControlTypes.FULLCONTROL_DENY)) {
            throw new ConditionParametersException();
        }
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        SecurityTokenManager securityTokenManager = this.factoryManager.getManager(SecurityTokenManager.class);

        ProcessTokenObject processToken = this.getCurrentProcessToken();
        AccountObject account = securityTokenManager.getAccount(processToken.getAccountID());
        List<GroupObject> groups = account.getGroups();

        List<UUID> accountGroupIDs = new ArrayList<>();
        groups.forEach(group -> accountGroupIDs.add(group.getID()));
        accountGroupIDs.add(account.getID());

        this.init();

        Map<UUID, Long> accessControl = new HashMap<>();
        List<SecurityDescriptorDefinition> securityDescriptors = new ArrayList<>();
        this.parents.forEach(securityDescriptor -> securityDescriptors.add(securityDescriptor.securityDescriptor));
        securityDescriptors.add(this.securityDescriptor);
        for (SecurityDescriptorDefinition securityDescriptor : securityDescriptors) {
            if (!securityDescriptor.isInherit()) {
                accessControl.clear();
            }

            for (Map.Entry<UUID, Long> pair : securityDescriptor.getAccessControl().entrySet()) {
                if (accessControl.containsKey(pair.getKey())) {
                    accessControl.put(pair.getKey(), LogicalUtils.or(accessControl.get(pair.getKey()), pair.getValue()));
                } else {
                    accessControl.put(pair.getKey(), pair.getValue());
                }
            }
        }

        boolean allow = false;

        for (UUID id : accountGroupIDs) {
            if (accessControl.containsKey(id)) {
                long accessControlTypeValue = accessControl.get(id);
                if (LogicalUtils.isAllExist(accessControlTypeValue, accessControlType)) {
                    allow = true;
                }
                if (LogicalUtils.isAnyExist(accessControlTypeValue, accessControlType << 1)) {
                    return false;
                }
            }
        }

        return allow;
    }

    public void checkAccessControlType(long accessControlType) {
        ProcessTokenObject processToken = this.getCurrentProcessToken();
        if (processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)) {
            return;
        }

        if (!this.isAccessControlType(accessControlType)) {
            throw new ConditionPermissionsException();
        }
    }

    public List<SecurityDescriptorSummaryDefinition> getAccessControlType() {
        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS) && !this.securityDescriptor.getOwners().contains(processToken.getAccountID()) && !this.isAccessControlType(AccessControlTypes.READPERMISSIONDESCRIPTOR_ALLOW)) {
            throw new ConditionPermissionsException();
        }

        List<SecurityDescriptorSummaryDefinition> securityDescriptorSummaries = new ArrayList<>();
        this.parents.forEach(securityDescriptor -> {
            SecurityDescriptorSummaryDefinition securityDescriptorSummary = new SecurityDescriptorSummaryDefinition();
            securityDescriptorSummary.getIdentifications().addAll(securityDescriptor.identifications);
            securityDescriptorSummary.setInherit(securityDescriptor.securityDescriptor.isInherit());
            securityDescriptorSummary.getAccessControl().putAll(securityDescriptor.securityDescriptor.getAccessControl());
            securityDescriptorSummaries.add(securityDescriptorSummary);
        });
        SecurityDescriptorSummaryDefinition securityDescriptorSummary = new SecurityDescriptorSummaryDefinition();
        securityDescriptorSummary.getIdentifications().addAll(this.identifications);
        securityDescriptorSummary.setInherit(this.securityDescriptor.isInherit());
        securityDescriptorSummary.getAccessControl().putAll(this.securityDescriptor.getAccessControl());
        securityDescriptorSummaries.add(securityDescriptorSummary);

        return securityDescriptorSummaries;
    }

    public void setInherit(boolean inherit) {
        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS) && !this.securityDescriptor.getOwners().contains(processToken.getAccountID()) && !this.isAccessControlType(AccessControlTypes.CHANGEPERMISSIONDESCRIPTOR_ALLOW)) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.securityDescriptor.setInherit(inherit);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void setOwners(List<UUID> owners) {
        if (ObjectUtils.isAnyNull(owners) || owners.isEmpty()) {
            throw new ConditionParametersException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS) && !this.securityDescriptor.getOwners().contains(processToken.getAccountID()) && !this.isAccessControlType(AccessControlTypes.TAKEONWERSHIP_ALLOW)) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.securityDescriptor.getOwners().clear();
        this.securityDescriptor.getOwners().addAll(owners);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void setAccessControlTypes(Map<UUID, Long> accessControl) {
        if (ObjectUtils.isAnyNull(accessControl)) {
            throw new ConditionParametersException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS) && !this.securityDescriptor.getOwners().contains(processToken.getAccountID()) && !this.isAccessControlType(AccessControlTypes.CHANGEPERMISSIONDESCRIPTOR_ALLOW)) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.securityDescriptor.getAccessControl().clear();
        this.securityDescriptor.getAccessControl().putAll(accessControl);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void checkRoleTypes(UUID roleType) {
        if (UUIDUtils.isAnyNullOrEmpty(roleType)) {
            throw new ConditionParametersException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        this.init();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS) && !this.securityDescriptor.getRoles().contains(roleType)) {
            throw new ConditionPermissionsException();
        }
    }

    public Set<UUID> getRoleTypes() {
        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS) && !this.securityDescriptor.getOwners().contains(processToken.getAccountID())) {
            throw new ConditionPermissionsException();
        }

        this.init();

        return Collections.unmodifiableSet(securityDescriptor.getRoles());
    }

    public void setRoleTypes(List<UUID> roleTypes) {
        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS) && !this.securityDescriptor.getOwners().contains(processToken.getAccountID())) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.securityDescriptor.getRoles().clear();
        this.securityDescriptor.getRoles().addAll(roleTypes);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void writeAudit(long accessControlType) {
        if (!this.audit) {
            throw new StatusNotSupportedException();
        }

        if (LogicalUtils.isNotAllExist(this.securityDescriptor.getAuditTypes(), accessControlType)) {
            return;
        }

        throw new StatusNotSupportedException();
        //...
    }

    public long getAuditTypes() {
        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS) && !this.securityDescriptor.getOwners().contains(processToken.getAccountID())) {
            throw new ConditionPermissionsException();
        }

        this.init();

        return securityDescriptor.getAuditTypes();
    }

    public void setAuditTypes(long auditTypes) {
        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS) && !this.securityDescriptor.getOwners().contains(processToken.getAccountID())) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.securityDescriptor.setAuditTypes(auditTypes);

        this.fresh();
        this.lock(LockTypes.NONE);
    }
}
