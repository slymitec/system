package indi.sly.system.services.center.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.*;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.center.prototypes.processors.ACenterInitializer;
import indi.sly.system.services.center.values.CenterAttributeType;
import indi.sly.system.services.center.values.CenterDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CenterBuilder extends APrototype {
    protected CenterFactory factory;

    public CenterObject create(String name, long attribute, UUID processID, ACenterInitializer initializer) {
        if (StringUtil.isNameIllegal(name) || ObjectUtil.isAnyNull(initializer)) {
            throw new ConditionParametersException();
        }

        CenterRepositoryObject centerRepository =
                this.factoryManager.getCorePrototypeRepository().get(SpaceType.KERNEL, CenterRepositoryObject.class);

        if (centerRepository.getCenterIDs().containsKey(name)) {
            throw new StatusAlreadyExistedException();
        }

        CenterDefinition center = new CenterDefinition();

        center.setID(UUIDUtil.createRandom());
        center.setAttribute(attribute);
        center.setName(name);
        if (LogicalUtil.isAnyExist(center.getAttribute(), CenterAttributeType.HAS_PROCESS)
                && !ValueUtil.isAnyNullOrEmpty(processID)) {
            center.setProcessID(processID);
        }
        center.setInitializer(initializer);

        centerRepository.getCenters().put(center.getID(), center);
        centerRepository.getCenterIDs().put(center.getName(), center.getID());

        return this.factory.build(center);
    }

    public void delete(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        CenterRepositoryObject centerRepository =
                this.factoryManager.getCorePrototypeRepository().get(SpaceType.KERNEL, CenterRepositoryObject.class);

        if (!centerRepository.getCenters().containsKey(id)) {
            throw new StatusNotExistedException();
        }

        CenterDefinition center = centerRepository.getCenters().getOrDefault(id, null);

        if (ObjectUtil.isAnyNull(center)) {
            throw new StatusNotExistedException();
        }

        centerRepository.getCenters().remove(center.getID());
        centerRepository.getCenterIDs().remove(center.getName());
    }
}
