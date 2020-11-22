package indi.sly.system.kernel.processes.prototypes;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

import indi.sly.system.common.support.IDeepCloneable;
import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.UUIDUtils;

public class ProcessTokenDefinition implements ISerializable<ProcessTokenDefinition> {
    public ProcessTokenDefinition() {
        this.roles = new HashSet<>();
    }

    private UUID accountID;
    private long privilegeTypes;
    private Set<UUID> roles;

    public UUID getAccountID() {
        return this.accountID;
    }

    public void setAccountID(UUID accountID) {
        this.accountID = accountID;
    }

    public long getPrivilegeTypes() {
        return this.privilegeTypes;
    }

    public void setPrivilegeTypes(long privilegeTypes) {
        this.privilegeTypes = privilegeTypes;
    }

    public Set<UUID> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<UUID> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessTokenDefinition that = (ProcessTokenDefinition) o;
        return privilegeTypes == that.privilegeTypes &&
                Objects.equals(accountID, that.accountID) &&
                roles.equals(that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountID, privilegeTypes, roles);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public ProcessTokenDefinition deepClone() {
        ProcessTokenDefinition processToken = new ProcessTokenDefinition();

        processToken.accountID = this.accountID;
        processToken.privilegeTypes = this.privilegeTypes;
        processToken.roles.addAll(this.roles);

        return processToken;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.accountID = UUIDUtils.readExternal(in);
        this.privilegeTypes = NumberUtils.readExternalLong(in);

        int valueInteger;

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.roles.add(UUIDUtils.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtils.writeExternal(out, this.accountID);
        NumberUtils.writeExternalLong(out, this.privilegeTypes);

        NumberUtils.writeExternalInteger(out, this.roles.size());
        for (UUID pair : this.roles) {
            UUIDUtils.writeExternal(out, pair);
        }
    }
}
