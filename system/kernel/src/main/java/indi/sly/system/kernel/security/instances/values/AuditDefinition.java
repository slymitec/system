package indi.sly.system.kernel.security.instances.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.security.values.UserIDDefinition;

import java.util.*;

public class AuditDefinition extends ADefinition {
    public AuditDefinition() {
        this.userIDs = new HashSet<>();
    }

    private UUID processID;
    private UUID accountID;
    private PathDefinition path;
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

    public PathDefinition getPath() {
        return this.path;
    }

    public void setPath(PathDefinition path) {
        this.path = path;
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
        if (o == null || getClass() != o.getClass()) return false;
        AuditDefinition that = (AuditDefinition) o;
        return audit == that.audit && Objects.equals(processID, that.processID) && Objects.equals(accountID, that.accountID) && Objects.equals(path, that.path) && Objects.equals(userIDs, that.userIDs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processID, accountID, path, userIDs, audit);
    }
}
