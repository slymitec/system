package indi.sly.system.kernel.security;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.UUIDUtils;

public class TokenDefinition implements ISerializable {
    private UUID accountID;
    private long privilegeTypes;

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

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.accountID = UUIDUtils.readExternal(in);
        this.privilegeTypes = NumberUtils.readExternalLong(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtils.writeExternal(out, this.accountID);
        NumberUtils.writeExternalLong(out, this.privilegeTypes);
    }
}
