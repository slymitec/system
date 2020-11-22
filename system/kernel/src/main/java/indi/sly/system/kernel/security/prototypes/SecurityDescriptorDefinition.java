package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.UUIDUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;
import java.util.Map.Entry;

public class SecurityDescriptorDefinition implements ISerializable<SecurityDescriptorDefinition> {
    private static final long serialVersionUID = 1L;

    private boolean inherit;
    private final Set<UUID> owners;
    private final Map<UUID, Long> accessControl;
    private final Set<UUID> roles;
    private long auditTypes;

    public SecurityDescriptorDefinition() {
        this.inherit = true;
        this.owners = new HashSet<>();
        this.accessControl = new HashMap<>();
        this.roles = new HashSet<>();
        this.auditTypes = AuditTypes.NULL;
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

    public Map<UUID, Long> getAccessControl() {
        return this.accessControl;
    }

    public Set<UUID> getRoles() {
        return this.roles;
    }

    public long getAuditTypes() {
        return this.auditTypes;
    }

    public void setAuditTypes(long auditTypes) {
        this.auditTypes = auditTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurityDescriptorDefinition that = (SecurityDescriptorDefinition) o;
        return inherit == that.inherit &&
                auditTypes == that.auditTypes &&
                owners.equals(that.owners) &&
                accessControl.equals(that.accessControl) &&
                roles.equals(that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inherit, owners, accessControl, roles, auditTypes);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public SecurityDescriptorDefinition deepClone() {
        SecurityDescriptorDefinition securityDescriptor = new SecurityDescriptorDefinition();

        securityDescriptor.inherit = this.inherit;
        securityDescriptor.owners.addAll(this.owners);
        securityDescriptor.accessControl.putAll(this.accessControl);
        securityDescriptor.roles.addAll(this.roles);
        securityDescriptor.auditTypes = this.auditTypes;

        return securityDescriptor;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.inherit = NumberUtils.readExternalBoolean(in);

        int valueInteger;

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.owners.add(UUIDUtils.readExternal(in));
        }

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.accessControl.put(UUIDUtils.readExternal(in), NumberUtils.readExternalLong(in));
        }

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.roles.add(UUIDUtils.readExternal(in));
        }

        this.auditTypes = NumberUtils.readExternalLong(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtils.writeExternalBoolean(out, this.inherit);

        NumberUtils.writeExternalInteger(out, this.owners.size());
        for (UUID pair : this.owners) {
            UUIDUtils.writeExternal(out, pair);
        }

        NumberUtils.writeExternalInteger(out, this.accessControl.size());
        for (Entry<UUID, Long> pair : this.accessControl.entrySet()) {
            UUIDUtils.writeExternal(out, pair.getKey());
            NumberUtils.writeExternalLong(out, pair.getValue());
        }

        NumberUtils.writeExternalInteger(out, this.roles.size());
        for (UUID pair : this.roles) {
            UUIDUtils.writeExternal(out, pair);
        }

        NumberUtils.writeExternalLong(out, this.auditTypes);
    }
}
