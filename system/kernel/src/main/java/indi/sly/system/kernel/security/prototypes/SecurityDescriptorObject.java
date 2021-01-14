package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.lang.StatusInsufficientResourcesException;
import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockTypes;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessPrototype;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.SecurityTokenManager;
import indi.sly.system.kernel.security.values.SecurityDescriptorDefinition;
import indi.sly.system.kernel.security.values.SecurityDescriptorSummaryDefinition;
import indi.sly.system.kernel.security.types.AccessControlTypes;
import indi.sly.system.kernel.security.types.PrivilegeTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SecurityDescriptorObject extends ABytesValueProcessPrototype<SecurityDescriptorDefinition> {
    public SecurityDescriptorObject() {
        this.parents = new ArrayList<>();
        this.identifications = new ArrayList<>();
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
        if (accessControlType == AccessControlTypes.NULL || LogicalUtil.isAnyExist(accessControlType,
                AccessControlTypes.FULLCONTROL_DENY)) {
            throw new ConditionParametersException();
        }
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)) {
            return true;
        }

        List<SecurityDescriptorDefinition> securityDescriptors = new ArrayList<>();
        for (SecurityDescriptorObject pair : this.parents) {
            pair.init();

            securityDescriptors.add(pair.value);
        }

        this.init();

        securityDescriptors.add(this.value);

        Map<UUID, Long> accessControl = new HashMap<>();
        for (SecurityDescriptorDefinition securityDescriptor : securityDescriptors) {
            if (!securityDescriptor.isInherit()) {
                accessControl.clear();
            }

            for (Map.Entry<UUID, Long> pair : securityDescriptor.getAccessControl().entrySet()) {
                if (accessControl.containsKey(pair.getKey())) {
                    accessControl.put(pair.getKey(), LogicalUtil.or(accessControl.get(pair.getKey()),
                            pair.getValue()));
                } else {
                    accessControl.put(pair.getKey(), pair.getValue());
                }
            }
        }

        SecurityTokenManager securityTokenManager = this.factoryManager.getManager(SecurityTokenManager.class);

        AccountObject account = securityTokenManager.getAccount(processToken.getAccountID());
        List<GroupObject> groups = account.getGroups();
        List<UUID> accountGroupIDs = new ArrayList<>();
        for (GroupObject group : groups) {
            accountGroupIDs.add(group.getID());
        }
        accountGroupIDs.add(account.getID());

        boolean allow = false;

        for (UUID id : accountGroupIDs) {
            if (accessControl.containsKey(id)) {
                long accessControlTypeValue = accessControl.get(id);
                if (LogicalUtil.isAllExist(accessControlTypeValue, accessControlType)) {
                    allow = true;
                }
                if (LogicalUtil.isAnyExist(accessControlTypeValue, accessControlType << 1)) {
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
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())
                && !this.isAccessControlType(AccessControlTypes.READPERMISSIONDESCRIPTOR_ALLOW)) {
            throw new ConditionPermissionsException();
        }

        List<SecurityDescriptorSummaryDefinition> securityDescriptorSummaries = new ArrayList<>();

        for (SecurityDescriptorObject pair : this.parents) {
            pair.init();

            SecurityDescriptorSummaryDefinition securityDescriptorSummary = new SecurityDescriptorSummaryDefinition();
            securityDescriptorSummary.getIdentifications().addAll(pair.identifications);
            securityDescriptorSummary.setInherit(pair.value.isInherit());
            securityDescriptorSummary.getAccessControl().putAll(pair.value.getAccessControl());
            securityDescriptorSummaries.add(securityDescriptorSummary);
        }

        this.init();

        SecurityDescriptorSummaryDefinition securityDescriptorSummary = new SecurityDescriptorSummaryDefinition();
        securityDescriptorSummary.getIdentifications().addAll(this.identifications);
        securityDescriptorSummary.setInherit(this.value.isInherit());
        securityDescriptorSummary.getAccessControl().putAll(this.value.getAccessControl());
        securityDescriptorSummaries.add(securityDescriptorSummary);

        return securityDescriptorSummaries;
    }

    public void setInherit(boolean inherit) {
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())
                && !this.isAccessControlType(AccessControlTypes.CHANGEPERMISSIONDESCRIPTOR_ALLOW)) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.value.setInherit(inherit);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void setOwners(List<UUID> owners) {
        if (ObjectUtil.isAnyNull(owners) || owners.isEmpty()) {
            throw new ConditionParametersException();
        }
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())
                && !this.isAccessControlType(AccessControlTypes.TAKEONWERSHIP_ALLOW)) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.value.getOwners().clear();
        this.value.getOwners().addAll(owners);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void setAccessControlTypes(Map<UUID, Long> accessControl) {
        if (ObjectUtil.isAnyNull(accessControl)) {
            throw new ConditionParametersException();
        }
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())
                && !this.isAccessControlType(AccessControlTypes.CHANGEPERMISSIONDESCRIPTOR_ALLOW)) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        Map<UUID, Long> resultAccessControl;
        if (this.value.isInherit()) {
            List<SecurityDescriptorDefinition> securityDescriptors = new ArrayList<>();
            for (SecurityDescriptorObject pair : this.parents) {
                pair.init();

                securityDescriptors.add(pair.value);
            }

            Map<UUID, Long> parentAccessControl = new HashMap<>();
            for (SecurityDescriptorDefinition securityDescriptor : securityDescriptors) {
                if (!securityDescriptor.isInherit()) {
                    parentAccessControl.clear();
                }

                for (Map.Entry<UUID, Long> pair : securityDescriptor.getAccessControl().entrySet()) {
                    if (parentAccessControl.containsKey(pair.getKey())) {
                        parentAccessControl.put(pair.getKey(), LogicalUtil.or(parentAccessControl.get(pair.getKey()),
                                pair.getValue()));
                    } else {
                        parentAccessControl.put(pair.getKey(), pair.getValue());
                    }
                }
            }

            resultAccessControl = new HashMap<>();

            for (Map.Entry<UUID, Long> pair : accessControl.entrySet()) {
                Long parentAccessControlValue = parentAccessControl.getOrDefault(pair.getKey(), null);
                if (ObjectUtil.isAnyNull(parentAccessControlValue)) {
                    resultAccessControl.put(pair.getKey(), pair.getValue());
                } else {
                    resultAccessControl.put(pair.getKey(), LogicalUtil.and(pair.getValue(),
                            ~(parentAccessControlValue.longValue())));
                }
            }
        } else {
            resultAccessControl = accessControl;
        }

        this.value.getAccessControl().clear();
        this.value.getAccessControl().putAll(resultAccessControl);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void checkRoleTypes(UUID roleType) {
        if (ValueUtil.isAnyNullOrEmpty(roleType)) {
            throw new ConditionParametersException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        this.init();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getRoles().contains(roleType)) {
            throw new ConditionPermissionsException();
        }
    }

    public Set<UUID> getRoleTypes() {
        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())) {
            throw new ConditionPermissionsException();
        }

        this.init();

        return Collections.unmodifiableSet(value.getRoles());
    }

    public void setRoleTypes(List<UUID> roleTypes) {
        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.value.getRoles().clear();
        this.value.getRoles().addAll(roleTypes);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void writeAudit(long accessControlType) {
        if (!this.audit) {
            throw new StatusNotSupportedException();
        }

        if (LogicalUtil.isNotAllExist(this.value.getAuditTypes(), accessControlType)) {
            return;
        }

        throw new StatusNotSupportedException();

        /* Write in FileSystem:
            /Files/Users/{Account Name}/Workspace/Archives/Logs/{LogID : String}.log
            {LogID : String} is String not UUID
        */
    }

    public long getAuditTypes() {
        if (!this.audit) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())) {
            throw new ConditionPermissionsException();
        }

        this.init();

        return value.getAuditTypes();
    }

    public void setAuditTypes(long auditTypes) {
        if (!this.audit) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.value.setAuditTypes(auditTypes);

        this.fresh();
        this.lock(LockTypes.NONE);
    }
}
