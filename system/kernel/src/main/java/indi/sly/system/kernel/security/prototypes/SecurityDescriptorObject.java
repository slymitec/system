package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.common.exceptions.StatusInsufficientResourcesException;
import indi.sly.system.common.exceptions.StatusNotSupportedException;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.prototypes.ABytesProcessObject;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.security.SecurityDescriptorSummaryDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    //

    public SecurityDescriptorSummaryDefinition getSummary() {
        return null;
    }

    //

    public void checkAccessControlType(long accessControlType) {
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        //...

    }

    public void writeAudit(long accessControlType) {
        if (!this.audit) {
            throw new StatusNotSupportedException();
        }

        //...
    }

    public void checkRoleTypes(UUID roleType) {
        if (UUIDUtils.isAnyNullOrEmpty(roleType)) {
            throw new ConditionParametersException();
        }

        this.init();

        if (!this.securityDescriptor.getRoles().contains(roleType)) {
            throw new ConditionPermissionsException();
        }
    }


    public void setInherit(boolean inherit) {

    }

    public void setOwners(List<UUID> owners) {
//        SecurityDescriptorDefinition securityDescriptor = this.analysis.getSecurityDescriptor();
//
//        Set<UUID> newOwners = securityDescriptor.getOwners();
//        newOwners.clear();
//        newOwners.addAll(owners);

        //analysis.setSecurityDescriptor(securityDescriptor);

        //1、特权检测，有

    }

    public void setAccessControlTypes(Map<UUID, Long> accessControl) {

    }

    public void setRoleTypes(List<UUID> roleTypes) {

    }
}
