package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.exceptions.StatusInsufficientResourcesException;
import indi.sly.system.common.exceptions.StatusNotSupportedException;
import indi.sly.system.common.functions.Consumer;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.prototypes.ABytesProcessObject;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.processes.ProcessThreadManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.SecurityDescriptorSummaryDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SecurityDescriptorObject extends ABytesProcessObject {
    public SecurityDescriptorObject() {
        this.parent = new ArrayList<>();
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

    private SecurityDescriptorDefinition securityDescriptor;
    private final List<Identification> identifications;
    protected final List<SecurityDescriptorObject> parent;
    private boolean permission;
    private boolean audit;


    public void setParentSecurityDescriptor(SecurityDescriptorObject parentSecurityDescriptor) {
        this.parent.addAll(parentSecurityDescriptor.parent);
        this.parent.add(parentSecurityDescriptor);
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    public void setAudit(boolean audit) {
        this.audit = audit;
    }

    //

    private boolean checkThisAccessControlType(long accessControlType) {
        ProcessThreadManager processThreadManager = this.factoryManager.getManager(ProcessThreadManager.class);

        ProcessObject process = processThreadManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        return true;
    }

    public void checkAccessControlType(long accessControlType) {
        if (!this.permission) {
            throw new StatusNotSupportedException();
        }

        for (SecurityDescriptorObject pair : this.parent) {
            pair.checkThisAccessControlType(accessControlType);
        }



        //...
    }

    public void writeAudit(long accessControlType) {
        if (!this.audit) {
            throw new StatusNotSupportedException();
        }

        //...
    }

    public SecurityDescriptorSummaryDefinition getSummary() {
        return null;
    }

    public void setOwner(List<UUID> owners) {
//        SecurityDescriptorDefinition securityDescriptor = this.analysis.getSecurityDescriptor();
//
//        Set<UUID> newOwners = securityDescriptor.getOwners();
//        newOwners.clear();
//        newOwners.addAll(owners);

        //analysis.setSecurityDescriptor(securityDescriptor);

        //1、特权检测，有

    }

    private Object notFinished;

    // read, write SecurityDescriptor, owner, etc..
}
