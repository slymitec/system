package indi.sly.system.kernel.security.values;

import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.kernel.security.types.AuditTypes;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;
import java.util.Map.Entry;

public class SecurityDescriptorDefinition implements ISerializeCapable<SecurityDescriptorDefinition> {
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
    public SecurityDescriptorDefinition deepClone() {
        SecurityDescriptorDefinition definition = new SecurityDescriptorDefinition();

        definition.inherit = this.inherit;
        definition.owners.addAll(this.owners);
        definition.accessControl.putAll(this.accessControl);
        definition.roles.addAll(this.roles);
        definition.auditTypes = this.auditTypes;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.inherit = NumberUtil.readExternalBoolean(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.owners.add(UUIDUtil.readExternal(in));
        }

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.accessControl.put(UUIDUtil.readExternal(in), NumberUtil.readExternalLong(in));
        }

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.roles.add(UUIDUtil.readExternal(in));
        }

        this.auditTypes = NumberUtil.readExternalLong(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtil.writeExternalBoolean(out, this.inherit);

        NumberUtil.writeExternalInteger(out, this.owners.size());
        for (UUID pair : this.owners) {
            UUIDUtil.writeExternal(out, pair);
        }

        NumberUtil.writeExternalInteger(out, this.accessControl.size());
        for (Entry<UUID, Long> pair : this.accessControl.entrySet()) {
            UUIDUtil.writeExternal(out, pair.getKey());
            NumberUtil.writeExternalLong(out, pair.getValue());
        }

        NumberUtil.writeExternalInteger(out, this.roles.size());
        for (UUID pair : this.roles) {
            UUIDUtil.writeExternal(out, pair);
        }

        NumberUtil.writeExternalLong(out, this.auditTypes);
    }
}
