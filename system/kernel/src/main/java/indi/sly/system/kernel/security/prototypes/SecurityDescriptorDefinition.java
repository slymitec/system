package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.support.IDeepCloneable;
import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.UUIDUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;
import java.util.Map.Entry;

public class SecurityDescriptorDefinition implements IDeepCloneable<SecurityDescriptorDefinition>, ISerializable {
    private static final long serialVersionUID = 7385733211448716541L;

    private boolean inherit;
    private Set<UUID> owners;
    private Map<UUID, Long> accessControl;
    private Set<UUID> roles;
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
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        SecurityDescriptorDefinition other = (SecurityDescriptorDefinition) obj;

        if (other.inherit != this.inherit || other.auditTypes != this.auditTypes) {
            return false;
        }
        if (other.owners.size() != this.owners.size() || other.accessControl.size() != this.accessControl.size() || other.roles.size() != this.roles.size()) {
            return false;
        }
        for (UUID pair : this.owners) {
            if (!other.owners.contains(pair)) {
                return false;
            }
        }
        for (Entry<UUID, Long> pair : this.accessControl.entrySet()) {
            if (other.accessControl.containsKey(pair.getKey())) {
                return false;
            } else if (!other.accessControl.get(pair.getKey()).equals(pair.getValue())) {
                return false;
            }
        }
        for (UUID pair : this.roles) {
            if (!other.roles.contains(pair)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + (inherit ? 1231 : 1237);
        for (UUID pair : this.owners) {
            result += pair.hashCode();
        }
        for (Entry<UUID, Long> pair : this.accessControl.entrySet()) {
            result += pair.getKey().hashCode() ^ pair.getValue().hashCode();
        }
        for (UUID pair : this.roles) {
            result += pair.hashCode();
        }
        result = prime * result + (int) (auditTypes ^ (auditTypes >>> 32));

        return result;
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
        for (Entry<UUID, Long> pair : this.accessControl.entrySet()) {
            securityDescriptor.accessControl.put(pair.getKey(), pair.getValue());
        }
        securityDescriptor.roles.addAll(this.roles);
        securityDescriptor.auditTypes = this.auditTypes;

        return securityDescriptor;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int valueInteger;

        this.inherit = NumberUtils.readExternalBoolean(in);

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
