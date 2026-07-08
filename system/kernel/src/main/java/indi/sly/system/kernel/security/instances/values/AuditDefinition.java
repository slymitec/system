package indi.sly.system.kernel.security.instances.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.security.values.UserIdRecord;

import java.util.*;

public class AuditDefinition extends ADefinition {
    public AuditDefinition() {
        this.userIds = new HashSet<>();
    }

    private UUID processId;
    private UUID accountId;
    private PathRecord path;
    private final Set<UserIdRecord> userIds;
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

    public PathRecord getPath() {
        return this.path;
    }

    public void setPath(PathRecord path) {
        this.path = path;
    }

    public Set<UserIdRecord> getUserIds() {
        return this.userIds;
    }

    public long getAudit() {
        return this.audit;
    }

    public void setAudit(long audit) {
        this.audit = audit;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof AuditDefinition that)) return false;
        return audit == that.audit && Objects.equals(processId, that.processId) && Objects.equals(accountId, that.accountId) && Objects.equals(path, that.path) && Objects.equals(userIds, that.userIds);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(processId, accountId, path, userIds, audit);
    }
}
