package indi.sly.system.kernel.security.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.common.values.PathRecord;

import java.util.*;

public class SecurityDescriptorSummaryDefinition extends ADefinition {
    public SecurityDescriptorSummaryDefinition() {
        this.permissions = new HashSet<>();
        this.audits = new HashSet<>();
    }

    private PathRecord path;
    private boolean inherit;
    private boolean permission;
    private boolean audit;
    private final Set<AccessControlRecord> permissions;
    private final Set<AccessControlRecord> audits;

    public PathRecord getPath() {
        return this.path;
    }

    public void setPath(PathRecord path) {
        this.path = path;
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

    public Set<AccessControlRecord> getPermissions() {
        return this.permissions;
    }

    public Set<AccessControlRecord> getAudits() {
        return this.audits;
    }
}
