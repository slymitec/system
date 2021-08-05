package indi.sly.system.kernel.security.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class SecurityDescriptorDefinition extends ADefinition<SecurityDescriptorDefinition> {
    private boolean inherit;
    private boolean hasChild;
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

    public Set<UUID> getOwners() {
        return this.owners;
    }

    public Set<AccessControlDefinition> getPermissions() {
        return this.permissions;
    }

    public Set<AccessControlDefinition> getAudits() {
        return this.audits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurityDescriptorDefinition that = (SecurityDescriptorDefinition) o;
        return inherit == that.inherit && hasChild == that.hasChild && owners.equals(that.owners) && permissions.equals(that.permissions) && audits.equals(that.audits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inherit, hasChild, owners, permissions, audits);
    }

    @Override
    public SecurityDescriptorDefinition deepClone() {
        SecurityDescriptorDefinition definition = new SecurityDescriptorDefinition();

        definition.inherit = this.inherit;
        definition.hasChild = this.hasChild;
        definition.owners.addAll(this.owners);
        definition.permissions.addAll(this.permissions);
        definition.audits.addAll(this.audits);

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        this.inherit = NumberUtil.readExternalBoolean(in);
        this.hasChild = NumberUtil.readExternalBoolean(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.owners.add(UUIDUtil.readExternal(in));
        }

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.permissions.add(ObjectUtil.readExternal(in));
        }

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.audits.add(ObjectUtil.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        NumberUtil.writeExternalBoolean(out, this.inherit);
        NumberUtil.writeExternalBoolean(out, this.hasChild);

        NumberUtil.writeExternalInteger(out, this.owners.size());
        for (UUID pair : this.owners) {
            UUIDUtil.writeExternal(out, pair);
        }

        NumberUtil.writeExternalInteger(out, this.permissions.size());
        for (AccessControlDefinition pair : this.permissions) {
            ObjectUtil.writeExternal(out, pair);
        }

        NumberUtil.writeExternalInteger(out, this.audits.size());
        for (AccessControlDefinition pair : this.audits) {
            ObjectUtil.writeExternal(out, pair);
        }
    }
}
