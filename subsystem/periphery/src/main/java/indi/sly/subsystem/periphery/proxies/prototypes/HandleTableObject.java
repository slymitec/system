package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AChildDefinitionObject;
import indi.sly.subsystem.periphery.proxies.values.HandleTableDefinition;
import indi.sly.subsystem.periphery.proxies.values.RemoteTypes;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.LogicalUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HandleTableObject extends AChildDefinitionObject<HandleTableDefinition, ProcedureObject> {
    protected ProxyFactory factory;

    public Set<UUID> listHandles() {
        return this.definition.list();
    }

    public boolean isExistHandle(UUID handle) {
        return this.listHandles().contains(handle);
    }

    public RemoteObject get(UUID handle) {
        return this.definition.get(handle);
    }

    public void add(RemoteObject remote) {
        if (LogicalUtil.allNotEqual(remote.getType(), RemoteTypes.OBJECT)) {
            throw new StatusRelationshipErrorException();
        }

        this.definition.add(remote);
    }

    public void delete(UUID handle) {
        RemoteObject remote = this.get(handle);

        this.definition.delete(handle);

        remote.die();
    }

    public void deleteIfExpired() {
        for (UUID handle : List.copyOf(this.definition.list())) {
            RemoteObject remote = this.definition.get(handle);

            if (remote.isExpired()) {
                this.delete(handle);
            }
        }
    }

    public void deleteAll() {
        for (UUID handle : List.copyOf(this.definition.list())) {
            this.delete(handle);
        }
    }
}
