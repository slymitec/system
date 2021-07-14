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
    private final Set<AccessControlDefinition> accessControls;
    private final Set<UUID> roles;
    private long audits;

    public SecurityDescriptorDefinition() {
        this.inherit = true;
        this.hasChild = false;
        this.owners = new HashSet<>();
        this.accessControls = new HashSet<>();
        this.roles = new HashSet<>();
        this.audits = AuditTypes.NULL;
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

    public Set<AccessControlDefinition> getAccessControls() {
        return this.accessControls;
    }

    public Set<UUID> getRoles() {
        return this.roles;
    }

    public long getAudits() {
        return this.audits;
    }

    public void setAudits(long audits) {
        this.audits = audits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurityDescriptorDefinition that = (SecurityDescriptorDefinition) o;
        return inherit == that.inherit &&
                hasChild == that.hasChild &&
                audits == that.audits &&
                owners.equals(that.owners) &&
                accessControls.equals(that.accessControls) &&
                roles.equals(that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inherit, hasChild, owners, accessControls, roles, audits);
    }

    @Override
    public SecurityDescriptorDefinition deepClone() {
        SecurityDescriptorDefinition definition = new SecurityDescriptorDefinition();

        definition.inherit = this.inherit;
        definition.hasChild = this.hasChild;
        definition.owners.addAll(this.owners);
        definition.accessControls.addAll(this.accessControls);
        definition.roles.addAll(this.roles);
        definition.audits = this.audits;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.inherit = NumberUtil.readExternalBoolean(in);
        this.hasChild = NumberUtil.readExternalBoolean(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.owners.add(UUIDUtil.readExternal(in));
        }

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.accessControls.add(ObjectUtil.readExternal(in));
        }

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.roles.add(UUIDUtil.readExternal(in));
        }

        this.audits = NumberUtil.readExternalLong(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtil.writeExternalBoolean(out, this.inherit);
        NumberUtil.writeExternalBoolean(out, this.hasChild);

        NumberUtil.writeExternalInteger(out, this.owners.size());
        for (UUID pair : this.owners) {
            UUIDUtil.writeExternal(out, pair);
        }

        NumberUtil.writeExternalInteger(out, this.accessControls.size());
        for (AccessControlDefinition pair : this.accessControls) {
            ObjectUtil.writeExternal(out, pair);
        }

        NumberUtil.writeExternalInteger(out, this.roles.size());
        for (UUID pair : this.roles) {
            UUIDUtil.writeExternal(out, pair);
        }

        NumberUtil.writeExternalLong(out, this.audits);
    }
}
