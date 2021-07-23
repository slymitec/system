package indi.sly.system.kernel.security.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.common.values.IdentificationDefinition;

import java.util.*;

public class SecurityDescriptorSummaryDefinition extends ADefinition<SecurityDescriptorSummaryDefinition> {
    public SecurityDescriptorSummaryDefinition() {
        this.identifications = new ArrayList<>();
        this.permissions = new HashSet<>();
        this.audits = new HashSet<>();
    }

    private final List<IdentificationDefinition> identifications;
    private boolean inherit;
    private boolean permission;
    private boolean audit;
    private final Set<AccessControlDefinition> permissions;
    private final Set<AccessControlDefinition> audits;

    public List<IdentificationDefinition> getIdentifications() {
        return this.identifications;
    }

    public boolean isInherit() {
        return this.inherit;
    }

    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

    public boolean isPermission() {
        return this.permission;
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    public boolean isAudit() {
        return this.audit;
    }

    public void setAudit(boolean audit) {
        this.audit = audit;
    }

    public Set<AccessControlDefinition> getPermissions() {
        return this.permissions;
    }

    public Set<AccessControlDefinition> getAudits() {
        return this.audits;
    }
}
