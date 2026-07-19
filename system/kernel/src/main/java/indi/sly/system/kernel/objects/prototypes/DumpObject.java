package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.core.prototypes.ACacheableObject;
import indi.sly.system.kernel.objects.values.DumpCacheEntity;
import indi.sly.system.kernel.objects.values.InfoOpenRecord;
import indi.sly.system.kernel.security.values.SecurityDescriptorSummaryRecord;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DumpObject extends ACacheableObject<DumpCacheEntity> {
    public Map<Long, Long> getDate() {
        return CollectionUtil.unmodifiable(this.cache.getDate());
    }

    public UUID getProcessId() {
        return this.cache.getProcessId();
    }

    public UUID getAccountId() {
        return this.cache.getAccountId();
    }

    public PathRecord getPath() {
        return this.cache.getPath();
    }

    public InfoOpenRecord getInfoOpen() {
        return this.cache.getInfoOpen();
    }

    public List<SecurityDescriptorSummaryRecord> getSecurityDescriptorSummary() {
        return CollectionUtil.unmodifiable(this.cache.getSecurityDescriptorSummary());
    }
}
