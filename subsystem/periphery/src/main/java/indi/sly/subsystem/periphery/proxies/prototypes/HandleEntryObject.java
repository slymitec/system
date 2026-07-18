package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AChildDefinitionObject;
import indi.sly.subsystem.periphery.proxies.values.HandleEntryDefinition;
import indi.sly.system.common.supports.CollectionUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;


import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HandleEntryObject extends AChildDefinitionObject<HandleEntryDefinition, HandleTableObject> {
    public UUID getHandle() {
        return this.definition.getHandle();
    }

    public String getClazz() {
        return this.definition.getClazz();
    }

    public Map<Long, Long> getDate() {
        return CollectionUtil.unmodifiable(this.definition.getDate());
    }
}
