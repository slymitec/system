package indi.sly.system.kernel.objects.values;

import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.core.values.ACacheEntity;
import indi.sly.system.kernel.security.values.SecurityDescriptorSummaryRecord;
import org.jspecify.annotations.Nullable;
import org.redisson.api.annotation.REntity;

import java.util.*;

@REntity
public class DumpCacheEntity extends ACacheEntity {
    public DumpCacheEntity() {
        this.date = new HashMap<>();
        this.securityDescriptorSummary = new ArrayList<>();
    }

    private final Map<Long, Long> date;
    @SuppressWarnings("NullAway.Init")
    private UUID processId;
    @SuppressWarnings("NullAway.Init")
    private UUID accountId;
    @SuppressWarnings("NullAway.Init")
    private PathRecord path;
    @Nullable
    private InfoOpenRecord infoOpen;
    private final List<SecurityDescriptorSummaryRecord> securityDescriptorSummary;

    public Map<Long, Long> getDate() {
        return this.date;
    }

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

    public @Nullable InfoOpenRecord getInfoOpen() {
        return this.infoOpen;
    }

    public void setInfoOpen(@Nullable InfoOpenRecord infoOpen) {
        this.infoOpen = infoOpen;
    }

    public List<SecurityDescriptorSummaryRecord> getSecurityDescriptorSummary() {
        return this.securityDescriptorSummary;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DumpCacheEntity cache)) return false;
        return Objects.equals(id, cache.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
