package indi.sly.system.services.job.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusInsufficientResourcesException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.core.prototypes.AValueProcessPrototype;
import indi.sly.system.services.job.values.JobPointerDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobPointerObject extends AValueProcessPrototype<JobPointerDefinition> {
    public UUID getJobID() {
        this.init();

        return this.value.getJobID();
    }

    public int getLimit() {
        this.init();

        return this.value.getLimit();
    }

    public Map<UUID, Class<? extends APrototype>> getProtoTypes() {
        this.init();

        return CollectionUtil.unmodifiable(this.value.getPrototypes());
    }

    public void setProtoTypes(Map<UUID, Class<? extends APrototype>> prototypes) {
        if (ObjectUtil.isAnyNull(prototypes)) {
            throw new ConditionParametersException();
        }

        this.init();

        if (prototypes.size() > this.value.getLimit()) {
            throw new StatusInsufficientResourcesException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.getPrototypes().clear();
            this.value.getPrototypes().putAll(prototypes);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public Map<Long, Long> getDate() {
        this.init();

        return CollectionUtil.unmodifiable(this.value.getDate());
    }
}
