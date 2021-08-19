package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.prototypes.AValueProcessObject;
import indi.sly.system.kernel.objects.values.DumpDefinition;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import indi.sly.system.kernel.security.values.SecurityDescriptorSummaryDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DumpObject extends AValueProcessObject<DumpDefinition, InfoObject> {
    public Map<Long, Long> getDate() {
        this.init();

        return CollectionUtil.unmodifiable(this.value.getDate());
    }

    public UUID getProcessID() {
        this.init();

        return this.value.getProcessID();
    }

    public UUID getAccountID() {
        this.init();

        return this.value.getAccountID();
    }

    public List<IdentificationDefinition> getIdentifications() {
        this.init();

        return this.value.getIdentifications();
    }

    public InfoOpenDefinition getInfoOpen() {
        this.init();

        return this.value.getInfoOpen();
    }

    public List<SecurityDescriptorSummaryDefinition> getSecurityDescriptorSummary() {
        return CollectionUtil.unmodifiable(this.value.getSecurityDescriptorSummary());
    }
}
