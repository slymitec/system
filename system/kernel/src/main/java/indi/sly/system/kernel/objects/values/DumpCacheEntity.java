package indi.sly.system.kernel.objects.values;

import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.core.values.ACacheEntity;
import indi.sly.system.kernel.security.values.SecurityDescriptorSummaryRecord;
import org.redisson.api.annotation.REntity;

import java.util.*;

@REntity
public class DumpCacheEntity extends ACacheEntity {
    public DumpCacheEntity() {
        this.date = new HashMap<>();
        this.securityDescriptorSummary = new ArrayList<>();
    }

    private final Map<Long, Long> date;
    private UUID processID;
    private UUID accountID;
    private PathRecord path;
    private InfoOpenRecord infoOpen;
    private final List<SecurityDescriptorSummaryRecord> securityDescriptorSummary;

    public Map<Long, Long> getDate() {
        return this.date;
    }

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

    public PathRecord getPath() {
        return this.path;
    }

    public void setPath(PathRecord path) {
        this.path = path;
    }

    public InfoOpenRecord getInfoOpen() {
        return this.infoOpen;
    }

    public void setInfoOpen(InfoOpenRecord infoOpen) {
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
