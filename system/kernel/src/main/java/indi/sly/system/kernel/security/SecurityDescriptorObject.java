package indi.sly.system.kernel.security;

import indi.sly.system.common.exceptions.StatusNotSupportedException;
import indi.sly.system.common.functions.Consumer;
import indi.sly.system.kernel.core.prototypes.ACoreObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SecurityDescriptorObject extends ACoreObject {
    public SecurityDescriptorObject() {
        this.parentAnalyse = new ArrayList<>();
    }

    protected Consumer<Long> funcLock;
    protected SecurityDescriptorAnalysisDefinition analysis;
    protected final List<SecurityDescriptorAnalysisDefinition> parentAnalyse;
    private boolean permission;
    private boolean audit;

    public void setLock(Consumer<Long> funcLock) {
        this.funcLock = funcLock;
    }

    public void setSecurityDescriptor(SecurityDescriptorAnalysisDefinition securityDescriptorAnalysis) {
        this.analysis = securityDescriptorAnalysis;
    }

    public void setParentSecurityDescriptor(SecurityDescriptorObject securityDescriptor) {
        this.parentAnalyse.addAll(securityDescriptor.parentAnalyse);
        this.parentAnalyse.add(securityDescriptor.analysis);
    }


    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    public void setAudit(boolean audit) {
        this.audit = audit;
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

    public SecurityDescriptorSummaryDefinition getSummary() {
        return null;
    }

    public void setOwner(List<UUID> owners) {
        SecurityDescriptorDefinition securityDescriptor = this.analysis.getSecurityDescriptor();

        Set<UUID> newOwners = securityDescriptor.getOwners();
        newOwners.clear();
        newOwners.addAll(owners);

        analysis.setSecurityDescriptor(securityDescriptor);

        //1、特权检测，有

    }

    private Object notFinished;
    // read, write SecurityDescriptor, owner, etc..
}
