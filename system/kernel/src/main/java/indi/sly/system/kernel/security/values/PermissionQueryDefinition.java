package indi.sly.system.kernel.security.values;

import indi.sly.system.common.values.ADefinition;

public class PermissionQueryDefinition extends ADefinition<PermissionQueryDefinition> {
    public PermissionQueryDefinition() {
        this.privilege = true;
        this.role = true;
    }

    private boolean privilege;
    private boolean role;

    public boolean isPrivilege() {
        return this.privilege;
    }

    public void setPrivilege(boolean privilege) {
        this.privilege = privilege;
    }

    public boolean isRole() {
        return this.role;
    }

    public void setRole(boolean role) {
        this.role = role;
    }
}
