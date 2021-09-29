package indi.sly.system.kernel.security.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.ADefinition;
import indi.sly.system.common.values.IdentificationDefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurityDescriptorSummaryDefinition that = (SecurityDescriptorSummaryDefinition) o;
        return inherit == that.inherit && permission == that.permission && audit == that.audit && identifications.equals(that.identifications) && permissions.equals(that.permissions) && audits.equals(that.audits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifications, inherit, permission, audit, permissions, audits);
    }

    @Override
    public SecurityDescriptorSummaryDefinition deepClone() {
        SecurityDescriptorSummaryDefinition definition = new SecurityDescriptorSummaryDefinition();

        definition.identifications.addAll(this.identifications);
        definition.inherit = this.inherit;
        definition.permission = this.permission;
        definition.audit = this.audit;
        definition.permissions.addAll(this.permissions);
        definition.audits.addAll(this.audits);

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.identifications.add(ObjectUtil.readExternal(in));
        }

        this.inherit = NumberUtil.readExternalBoolean(in);
        this.permission = NumberUtil.readExternalBoolean(in);
        this.audit = NumberUtil.readExternalBoolean(in);

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

        NumberUtil.writeExternalInteger(out, this.identifications.size());
        for (IdentificationDefinition pair : this.identifications) {
            ObjectUtil.writeExternal(out, pair);
        }

        NumberUtil.writeExternalBoolean(out, this.inherit);
        NumberUtil.writeExternalBoolean(out, this.permission);
        NumberUtil.writeExternalBoolean(out, this.audit);

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
