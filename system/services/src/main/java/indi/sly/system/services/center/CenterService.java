package indi.sly.system.services.center;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.AService;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.services.center.prototypes.CenterBuilder;
import indi.sly.system.services.center.prototypes.CenterFactory;
import indi.sly.system.services.center.prototypes.CenterObject;
import indi.sly.system.services.center.prototypes.CenterRepositoryObject;
import indi.sly.system.services.center.prototypes.processors.ACenterInitializer;
import indi.sly.system.services.center.values.CenterDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CenterService extends AService {
    @Override
    public void startup(long startup) {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void check() {
    }

    protected CenterFactory factory;

    public CenterObject get(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        CenterRepositoryObject centerRepository =
                this.factoryManager.getCorePrototypeRepository().get(SpaceType.KERNEL, CenterRepositoryObject.class);

        CenterDefinition center = centerRepository.getCenters().getOrDefault(id, null);

        if (ObjectUtil.isAnyNull(center)) {
            throw new StatusNotExistedException();
        }

        return this.factory.build(center);
    }

    public CenterObject get(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        CenterRepositoryObject centerRepository =
                this.factoryManager.getCorePrototypeRepository().get(SpaceType.KERNEL, CenterRepositoryObject.class);

        UUID centerID = centerRepository.getCenterIDs().getOrDefault(name, null);

        if (ValueUtil.isAnyNullOrEmpty(centerID)) {
            throw new StatusNotExistedException();
        }

        CenterDefinition center = centerRepository.getCenters().getOrDefault(centerID, null);

        if (ObjectUtil.isAnyNull(center)) {
            throw new StatusNotExistedException();
        }

        return this.factory.build(center);
    }

    public CenterObject create(String name, long attribute, UUID processID, ACenterInitializer initializer) {
        CenterBuilder centerBuilder = this.factory.createCenter();

        return centerBuilder.create(name, attribute, processID, initializer);
    }

    public synchronized void delete(UUID id) {
        CenterBuilder centerBuilder = this.factory.createCenter();

        centerBuilder.delete(id);
    }
}
