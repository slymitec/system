package indi.sly.system.kernel.objects;

import java.util.List;

import javax.inject.Named;

import indi.sly.system.kernel.core.enviroment.types.SpaceTypes;
import indi.sly.system.kernel.memory.caches.prototypes.InfoCacheObject;
import indi.sly.system.kernel.objects.values.InfoStatusOpenDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.types.StartupTypes;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.prototypes.InfoFactoryObject;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ObjectManager extends AManager {
    @Override
    public void startup(long startupTypes) {
        if (startupTypes == StartupTypes.STEP_INIT) {

        } else if (startupTypes == StartupTypes.STEP_KERNEL) {
            InfoFactoryObject infoFactory = this.factoryManager.create(InfoFactoryObject.class);
            infoFactory.initInfoObjectFactory();

            InfoObject rootInfo = infoFactory.buildRootInfoObject();
            InfoCacheObject infoCache = this.factoryManager.getCoreRepository().get(SpaceTypes.KERNEL,
                    InfoCacheObject.class);
            infoCache.add(SpaceTypes.KERNEL, rootInfo);
        }
    }

    public InfoObject get(List<Identification> identifications) {
        if (ObjectUtils.isAnyNull(identifications) || identifications.size() > 256) {
            throw new ConditionParametersException();
        }

        InfoCacheObject infoCache = this.factoryManager.getCoreRepository().get(SpaceTypes.KERNEL,
                InfoCacheObject.class);

        InfoObject info = infoCache.getIfExisted(SpaceTypes.KERNEL,
                this.factoryManager.getKernelSpace().getConfiguration().OBJECTS_PROTOTYPE_ROOT_ID);

        for (Identification identification : identifications) {
            info = info.getChild(identification);
        }

        return info;
    }

    public InfoObject rebuild(List<Identification> identifications, InfoStatusOpenDefinition open) {
        if (ObjectUtils.isAnyNull(identifications, open)) {
            throw new ConditionParametersException();
        }

        InfoObject info;
        if (identifications.size() > 0) {
            info = this.get(identifications.subList(0, identifications.size() - 1));
            info = info.rebuildChild(identifications.get(identifications.size() - 1), open);
        } else {
            info = this.get(identifications);
        }

        return info;
    }
}
