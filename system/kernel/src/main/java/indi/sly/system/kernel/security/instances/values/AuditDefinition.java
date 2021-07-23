package indi.sly.system.kernel.security.instances.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;
import indi.sly.system.kernel.security.values.UserIDDefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class AuditDefinition extends ADefinition<AuditDefinition> {
    public AuditDefinition() {
        this.userIDs = new HashSet<>();
    }

    private UUID processID;
    private UUID accountID;
    private final Set<UserIDDefinition> userIDs;
    private long audit;

    public UUID getProcessID() {
        return this.processID;
    }

    public void setProcessID(UUID processID) {
        this.processID = processID;
    }

    public UUID getAccountID() {
        return this.accountID;
    }

    public void setAccountID(UUID accountID) {
        this.accountID = accountID;
    }

    public Set<UserIDDefinition> getUserIDs() {
        return this.userIDs;
    }

    public long getAudit() {
        return this.audit;
    }

    public void setAudit(long audit) {
        this.audit = audit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditDefinition that = (AuditDefinition) o;
        return audit == that.audit && Objects.equals(processID, that.processID) && Objects.equals(accountID, that.accountID) && userIDs.equals(that.userIDs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processID, accountID, userIDs, audit);
    }

    @Override
    public AuditDefinition deepClone() {
        AuditDefinition definition = new AuditDefinition();

        definition.processID = this.processID;
        definition.accountID = this.accountID;
        definition.userIDs.addAll(this.userIDs);
        definition.audit = this.audit;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.processID = UUIDUtil.readExternal(in);
        this.accountID = UUIDUtil.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.userIDs.add(ObjectUtil.readExternal(in));
        }

        this.audit = NumberUtil.readExternalLong(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtil.writeExternal(out, this.processID);
        UUIDUtil.writeExternal(out, this.accountID);

        NumberUtil.writeExternalInteger(out, this.userIDs.size());
        for (UserIDDefinition pair : this.userIDs) {
            ObjectUtil.writeExternal(out, pair);
        }

        NumberUtil.writeExternalLong(out, this.audit);
    }
}
