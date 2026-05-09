package indi.sly.system.kernel.objects.values;

import com.redis.om.spring.annotations.Document;
import indi.sly.system.common.values.IdentifierDefinition;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.values.ACacheEntity;
import indi.sly.system.kernel.security.values.SecurityDescriptorSummaryDefinition;

import java.util.*;

@Document("DumpObject")
public class DumpCacheEntity extends ACacheEntity {
    public DumpCacheEntity() {
        this.date = new HashMap<>();
        this.securityDescriptorSummary = new ArrayList<>();
    }

    private final Map<Long, Long> date;
    private UUID processID;
    private UUID accountID;
    private PathDefinition path;
    private InfoOpenDefinition infoOpen;
    private final List<SecurityDescriptorSummaryDefinition> securityDescriptorSummary;

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

    public PathDefinition getPath() {
        return this.path;
    }

    public void setPath(PathDefinition path) {
        this.path = path;
    }

    public InfoOpenDefinition getInfoOpen() {
        return this.infoOpen;
    }

    public void setInfoOpen(InfoOpenDefinition infoOpen) {
        this.infoOpen = infoOpen;
    }

    public List<SecurityDescriptorSummaryDefinition> getSecurityDescriptorSummary() {
        return this.securityDescriptorSummary;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DumpCacheEntity that = (DumpCacheEntity) o;
        return Objects.equals(date, that.date) && Objects.equals(processID, that.processID) && Objects.equals(accountID, that.accountID) && Objects.equals(path, that.path) && Objects.equals(infoOpen, that.infoOpen) && Objects.equals(securityDescriptorSummary, that.securityDescriptorSummary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), date, processID, accountID, path, infoOpen, securityDescriptorSummary);
    }
}
