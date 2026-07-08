package indi.sly.system.kernel.security.values;

import indi.sly.system.kernel.core.values.APersistentEntity;

import java.util.*;

public class SecurityDescriptorEntity extends APersistentEntity {
    public SecurityDescriptorEntity() {
        this.inherit = true;
        this.hasChild = false;
        this.owners = new HashSet<>();
        this.permissions = new HashSet<>();
        this.audits = new HashSet<>();
    }

    private boolean inherit;
    private boolean hasChild;
    private boolean canChangeOwner;
    private final Set<UUID> owners;
    private final Set<AccessControlRecord> permissions;
    private final Set<AccessControlRecord> audits;

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

    public Set<AccessControlRecord> getPermissions() {
        return this.permissions;
    }

    public Set<AccessControlRecord> getAudits() {
        return this.audits;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SecurityDescriptorEntity that)) return false;
        return inherit == that.inherit && hasChild == that.hasChild && canChangeOwner == that.canChangeOwner && Objects.equals(owners, that.owners) && Objects.equals(permissions, that.permissions) && Objects.equals(audits, that.audits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inherit, hasChild, canChangeOwner, owners, permissions, audits);
    }
}
