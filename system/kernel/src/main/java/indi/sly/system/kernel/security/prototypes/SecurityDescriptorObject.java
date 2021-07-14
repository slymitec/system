package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessPrototype;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.AccountGroupManager;
import indi.sly.system.kernel.security.values.AccessControlScopeTypes;
import indi.sly.system.kernel.security.values.UserTypes;
import indi.sly.system.kernel.security.values.AccessControlDefinition;
import indi.sly.system.kernel.security.values.SecurityDescriptorDefinition;
import indi.sly.system.kernel.security.values.SecurityDescriptorSummaryDefinition;
import indi.sly.system.kernel.security.values.AccessControlTypes;
import indi.sly.system.kernel.security.values.PrivilegeTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SecurityDescriptorObject extends ABytesValueProcessPrototype<SecurityDescriptorDefinition> {
    public SecurityDescriptorObject() {
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

    private List<IdentificationDefinition> identifications;
    private final List<SecurityDescriptorObject> parents;
    private boolean permission;
    private boolean audit;

    public void setIdentifications(List<IdentificationDefinition> identifications) {
        if (ObjectUtil.isAnyNull(this.identifications)) {
            throw new ConditionParametersException();
        }

        this.identifications = identifications;
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

        ProcessObject process = processManager.getCurrentProcess();

        return process.getToken();
    }

    private boolean allowAccessControl(long accessControl) {
        if (accessControl == AccessControlTypes.NULL || LogicalUtil.isAnyExist(accessControl,
                AccessControlTypes.FULLCONTROL_DENY)) {
            throw new ConditionParametersException();
        }
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (processToken.isPrivilegeType(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)) {
            return true;
        }

        List<SecurityDescriptorDefinition> securityDescriptors = new ArrayList<>();
        for (SecurityDescriptorObject pair : this.parents) {
            pair.init();

            securityDescriptors.add(pair.value);
        }

        this.init();

        securityDescriptors.add(this.value);

        Set<AccessControlDefinition> effectiveAccessControls = new HashSet<>();
        for (int i = 0; i < securityDescriptors.size(); i++) {
            if (!securityDescriptors.get(i).isInherit()) {
                effectiveAccessControls.clear();
            }

            for (AccessControlDefinition pair : securityDescriptors.get(i).getAccessControls()) {
                if (LogicalUtil.isAllExist(AccessControlScopeTypes.THIS, pair.getScope())) {
                    if (i == securityDescriptors.size() - 1) {
                        effectiveAccessControls.add(pair);
                    }
                }
                if (LogicalUtil.isAllExist(AccessControlScopeTypes.CHILD_HAS_CHILD, pair.getScope())) {
                    if (i == securityDescriptors.size() - 2 && securityDescriptors.get(i + 1).isHasChild()) {
                        effectiveAccessControls.add(pair);
                    }
                }
                if (LogicalUtil.isAllExist(AccessControlScopeTypes.CHILD_HAS_NOT_CHILD, pair.getScope())) {
                    if (i == securityDescriptors.size() - 2 && !securityDescriptors.get(i + 1).isHasChild()) {
                        effectiveAccessControls.add(pair);
                    }
                }
                if (LogicalUtil.isAllExist(AccessControlScopeTypes.HIERARCHICAL_HAS_CHILD, pair.getScope())) {
                    if (i < securityDescriptors.size() - 1 && securityDescriptors.get(securityDescriptors.size() - 1).isHasChild()) {
                        effectiveAccessControls.add(pair);
                    }
                }
                if (LogicalUtil.isAllExist(AccessControlScopeTypes.HIERARCHICAL_HAS_NOT_CHILD, pair.getScope())) {
                    if (i < securityDescriptors.size() - 1 && !securityDescriptors.get(securityDescriptors.size() - 1).isHasChild()) {
                        effectiveAccessControls.add(pair);
                    }
                }
            }
        }

        AccountGroupManager accountGroupManager = this.factoryManager.getManager(AccountGroupManager.class);

        AccountObject account = accountGroupManager.getAccount(processToken.getAccountID());
        UUID accountID = account.getID();
        List<GroupObject> groups = account.getGroups();
        List<UUID> groupIDs = new ArrayList<>();
        for (GroupObject group : groups) {
            groupIDs.add(group.getID());
        }

        boolean allow = false;

        for (AccessControlDefinition pair : effectiveAccessControls) {
            if (pair.getType() == UserTypes.GROUP) {
                if (groupIDs.contains(pair.getId())) {
                    if (LogicalUtil.isAllExist(accessControl, pair.getValue())) {
                        allow = true;
                    }
                    if (LogicalUtil.isAnyExist(pair.getValue(), accessControl << 1)) {
                        return false;
                    }
                }
            } else if (pair.getType() == UserTypes.ACCOUNT) {
                if (accountID.equals(pair.getId())) {
                    if (LogicalUtil.isAllExist(accessControl, pair.getValue())) {
                        allow = true;
                    }
                    if (LogicalUtil.isAnyExist(pair.getValue(), accessControl << 1)) {
                        return false;
                    }
                }
            }
        }

        return allow;
    }

    public void check(long accessControl) {
        ProcessTokenObject processToken = this.getCurrentProcessToken();
        if (processToken.isPrivilegeType(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)) {
            return;
        }

        if (!this.allowAccessControl(accessControl) || !processToken.getRoles().containsAll(this.getRoles())) {
            throw new ConditionPermissionsException();
        }
    }

    public List<SecurityDescriptorSummaryDefinition> getSummary() {
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeType(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())
                && !this.allowAccessControl(AccessControlTypes.READPERMISSIONDESCRIPTOR_ALLOW)) {
            throw new ConditionPermissionsException();
        }

        List<SecurityDescriptorSummaryDefinition> securityDescriptorSummaries = new ArrayList<>();

        for (SecurityDescriptorObject pair : this.parents) {
            pair.init();

            SecurityDescriptorSummaryDefinition securityDescriptorSummary = new SecurityDescriptorSummaryDefinition();
            securityDescriptorSummary.getIdentifications().addAll(pair.identifications);
            securityDescriptorSummary.setInherit(pair.value.isInherit());
            securityDescriptorSummary.getAccessControls().addAll(pair.value.getAccessControls());
            securityDescriptorSummaries.add(securityDescriptorSummary);
        }

        this.init();

        SecurityDescriptorSummaryDefinition securityDescriptorSummary = new SecurityDescriptorSummaryDefinition();
        securityDescriptorSummary.getIdentifications().addAll(this.identifications);
        securityDescriptorSummary.setInherit(this.value.isInherit());
        securityDescriptorSummary.getAccessControls().addAll(this.value.getAccessControls());
        securityDescriptorSummaries.add(securityDescriptorSummary);

        return securityDescriptorSummaries;
    }

    public boolean isInherit() {
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeType(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())
                && !this.allowAccessControl(AccessControlTypes.READPERMISSIONDESCRIPTOR_ALLOW)) {
            throw new ConditionPermissionsException();
        }

        this.init();

        return this.value.isInherit();
    }

    public void setInherit(boolean inherit) {
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeType(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())
                && !this.allowAccessControl(AccessControlTypes.CHANGEPERMISSIONDESCRIPTOR_ALLOW)) {
            throw new ConditionPermissionsException();
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

        if (!processToken.isPrivilegeType(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())
                && !this.allowAccessControl(AccessControlTypes.READPERMISSIONDESCRIPTOR_ALLOW)) {
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

        if (!processToken.isPrivilegeType(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())
                && !this.allowAccessControl(AccessControlTypes.READPERMISSIONDESCRIPTOR_ALLOW)) {
            throw new ConditionPermissionsException();
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

        if (!processToken.isPrivilegeType(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())
                && !this.allowAccessControl(AccessControlTypes.TAKEONWERSHIP_ALLOW)) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.getOwners().clear();
        this.value.getOwners().addAll(owners);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public void setAccessControls(Set<AccessControlDefinition> accessControls) {
        if (ObjectUtil.isAnyNull(accessControls)) {
            throw new ConditionParametersException();
        }
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        for (AccessControlDefinition accessControl : accessControls) {
            if (!this.value.isHasChild() && LogicalUtil.isAnyExist(accessControl.getScope(),
                    LogicalUtil.or(AccessControlScopeTypes.CHILD_HAS_CHILD,
                            AccessControlScopeTypes.CHILD_HAS_NOT_CHILD,
                            AccessControlScopeTypes.HIERARCHICAL_HAS_CHILD,
                            AccessControlScopeTypes.HIERARCHICAL_HAS_NOT_CHILD))) {
                throw new ConditionParametersException();
            }
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeType(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())
                && !this.allowAccessControl(AccessControlTypes.CHANGEPERMISSIONDESCRIPTOR_ALLOW)) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.getAccessControls().clear();
        this.value.getAccessControls().addAll(accessControls);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public Set<UUID> getRoles() {
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        this.init();

        return Collections.unmodifiableSet(value.getRoles());
    }

    public void setRoles(List<UUID> roleTypes) {
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeType(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.getRoles().clear();
        this.value.getRoles().addAll(roleTypes);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public void writeAudit(long accessControl) {
        if (!this.permission || !this.audit) {
            throw new StatusNotSupportedException();
        }

        if (LogicalUtil.isNotAllExist(this.value.getAudits(), accessControl)) {
            return;
        }

        throw new StatusNotSupportedException();

        /* Write in FileSystem:
            /Files/Users/{Account Name}/Workspace/Archives/Logs/{LogID : String}.log
            {LogID : String} is String not UUID
        */
    }

    public long getAudits() {
        if (!this.permission || !this.audit) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeType(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())) {
            throw new ConditionPermissionsException();
        }

        this.init();

        return value.getAudits();
    }

    public void setAudits(long audits) {
        if (!this.permission || !this.audit) {
            throw new StatusNotSupportedException();
        }

        ProcessTokenObject processToken = this.getCurrentProcessToken();

        if (!processToken.isPrivilegeType(PrivilegeTypes.OBJECTS_ACCESS_INFOOBJECTS)
                && !this.value.getOwners().contains(processToken.getAccountID())) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setAudits(audits);

        this.fresh();
        this.lock(LockType.NONE);
    }
}
