package indi.sly.system.kernel.objects;

import java.util.List;

import javax.inject.Named;

import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.memory.caches.prototypes.InfoCacheObject;
import indi.sly.system.kernel.objects.values.InfoStatusOpenDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.prototypes.InfoFactory;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ObjectManager extends AManager {
    @Override
    public void startup(long startupTypes) {
        if (startupTypes == StartupType.STEP_INIT) {
        } else if (startupTypes == StartupType.STEP_KERNEL) {
            InfoFactory infoFactory = this.factoryManager.create(InfoFactory.class);
            infoFactory.init();

            InfoObject rootInfo = infoFactory.buildRootInfo();
            InfoCacheObject infoCache = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                    InfoCacheObject.class);
            infoCache.add(SpaceType.KERNEL, rootInfo);
        }
    }

    @Override
    public void shutdown() {
    }

    public InfoObject get(List<IdentificationDefinition> identifications) {
        if (ObjectUtil.isAnyNull(identifications) || identifications.size() > 256) {
            throw new ConditionParametersException();
        }

        InfoCacheObject infoCache = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                InfoCacheObject.class);

        InfoObject info = infoCache.getIfExisted(SpaceType.KERNEL,
                this.factoryManager.getKernelSpace().getConfiguration().OBJECTS_PROTOTYPE_ROOT_ID);

        for (IdentificationDefinition identification : identifications) {
            info = info.getChild(identification);
        }

        return info;
    }

    public InfoObject rebuild(List<IdentificationDefinition> identifications, InfoStatusOpenDefinition open) {
        if (ObjectUtil.isAnyNull(identifications, open)) {
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
