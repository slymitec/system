package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AValueProcessObject;
import indi.sly.system.kernel.objects.values.DumpDefinition;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import indi.sly.system.kernel.security.values.SecurityDescriptorSummaryDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DumpObject extends AValueProcessObject<DumpDefinition, InfoObject> {
    public Map<Long, Long> getDate() {
        try {
            this.lock(LockType.READ);
            this.init();

            return CollectionUtil.unmodifiable(this.value.getDate());
        } finally {
            this.unlock(LockType.READ);
        }
    }

    public UUID getProcessID() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getProcessID();
        } finally {
            this.unlock(LockType.READ);
        }
    }

    public UUID getAccountID() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getAccountID();
        } finally {
            this.unlock(LockType.READ);
        }
    }

    public List<IdentificationDefinition> getIdentifications() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getIdentifications();
        } finally {
            this.unlock(LockType.READ);
        }
    }

    public InfoOpenDefinition getInfoOpen() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getInfoOpen();
        } finally {
            this.unlock(LockType.READ);
        }
    }

    public List<SecurityDescriptorSummaryDefinition> getSecurityDescriptorSummary() {
        try {
            this.lock(LockType.READ);
            this.init();

            return CollectionUtil.unmodifiable(this.value.getSecurityDescriptorSummary());
        } finally {
            this.unlock(LockType.READ);
        }
    }
}
