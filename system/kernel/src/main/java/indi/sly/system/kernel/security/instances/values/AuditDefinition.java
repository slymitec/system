package indi.sly.system.kernel.security.instances.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.security.values.UserIdDefinition;

import java.util.*;

public class AuditDefinition extends ADefinition {
    public AuditDefinition() {
        this.userIds = new HashSet<>();
    }

    private UUID processId;
    private UUID accountId;
    private PathDefinition path;
    private final Set<UserIdDefinition> userIds;
    private long audit;

    public UUID getProcessId() {
        return this.processId;
    }

    public void setProcessId(UUID processId) {
        this.processId = processId;
    }

    public UUID getAccountId() {
        return this.accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public PathDefinition getPath() {
        return this.path;
    }

    public void setPath(PathDefinition path) {
        this.path = path;
    }

    public Set<UserIdDefinition> getUserIds() {
        return this.userIds;
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
        return audit == that.audit && Objects.equals(processId, that.processId) && Objects.equals(accountId, that.accountId) && Objects.equals(path, that.path) && Objects.equals(userIds, that.userIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processId, accountId, path, userIds, audit);
    }
}
