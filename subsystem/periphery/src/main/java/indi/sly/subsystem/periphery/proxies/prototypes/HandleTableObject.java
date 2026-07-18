package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AChildDefinitionObject;
import indi.sly.subsystem.periphery.proxies.values.HandleEntryDefinition;
import indi.sly.subsystem.periphery.proxies.values.HandleTableDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HandleTableObject extends AChildDefinitionObject<HandleTableDefinition, ProcedureObject> {
    protected ProxyFactory factory;

    public Set<UUID> getAllHandles() {
        return this.definition.list();
    }

    public HandleEntryObject get(UUID handle) {
        HandleEntryDefinition handleEntry = this.definition.get(handle);

        return this.factory.buildHandleEntry(handleEntry, this);
    }
}
