package indi.sly.system.kernel.security.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class SecurityDescriptorDefinition extends ADefinition {
    private boolean inherit;
    private boolean hasChild;
    private boolean canChangeOwner;
    private final Set<UUID> owners;
    private final Set<AccessControlDefinition> permissions;
    private final Set<AccessControlDefinition> audits;

    public SecurityDescriptorDefinition() {
        this.inherit = true;
        this.hasChild = false;
        this.owners = new HashSet<>();
        this.permissions = new HashSet<>();
        this.audits = new HashSet<>();
    }

    public boolean isHasChild() {
        return this.hasChild;
    }

    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    public boolean isInherit() {
        return this.inherit;
    }

    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

    public boolean isCanChangeOwner() {
        return this.canChangeOwner;
    }

    public void setCanChangeOwner(boolean canChangeOwner) {
        this.canChangeOwner = canChangeOwner;
    }

    public Set<UUID> getOwners() {
        return this.owners;
    }

    public Set<AccessControlDefinition> getPermissions() {
        return this.permissions;
    }

    public Set<AccessControlDefinition> getAudits() {
        return this.audits;
    }
}
