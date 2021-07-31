package indi.sly.system.services.center;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.AService;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.services.center.prototypes.CenterFactory;
import indi.sly.system.services.center.prototypes.CenterObject;
import indi.sly.system.services.center.prototypes.CenterRepositoryObject;
import indi.sly.system.services.center.prototypes.wrappers.ACenterInitializer;
import indi.sly.system.services.center.values.CenterDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import javax.transaction.Transactional;
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
                this.factoryManager.getCoreRepository().get(SpaceType.KERNEL, CenterRepositoryObject.class);

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
                this.factoryManager.getCoreRepository().get(SpaceType.KERNEL, CenterRepositoryObject.class);

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

    public synchronized void create(String name, long attribute, UUID processID, ACenterInitializer initializer) {
        CenterRepositoryObject centerRepository =
                this.factoryManager.getCoreRepository().get(SpaceType.KERNEL, CenterRepositoryObject.class);

        if (centerRepository.getCenterIDs().containsKey(name)) {
            throw new StatusAlreadyExistedException();
        }

        CenterDefinition center = new CenterDefinition();

        center.setID(UUIDUtil.createRandom());
        center.setAttribute(attribute);
        center.setName(name);
        center.setProcessID(processID);
        center.setInitializer(initializer);

        centerRepository.getCenters().put(center.getID(), center);
        centerRepository.getCenterIDs().put(center.getName(), center.getID());
    }

    public synchronized void delete(UUID id) {
        CenterRepositoryObject centerRepository =
                this.factoryManager.getCoreRepository().get(SpaceType.KERNEL, CenterRepositoryObject.class);

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
