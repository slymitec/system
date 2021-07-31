package indi.sly.system.services.center.prototypes;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.center.values.CenterDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CenterRepositoryObject extends APrototype {
    public CenterRepositoryObject() {
        this.centers = new ConcurrentHashMap<>();
        this.centerIDs = new ConcurrentHashMap<>();
    }

    private final Map<UUID, CenterDefinition> centers;
    private final Map<String, UUID> centerIDs;

    public Map<UUID, CenterDefinition> getCenters() {
        return this.centers;
    }

    public Map<String, UUID> getCenterIDs() {
        return this.centerIDs;
    }
}
