package indi.sly.system.kernel.objects;

import java.util.List;

import javax.inject.Named;

import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.memory.caches.prototypes.InfoCacheObject;
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
    public void startup(long startup) {
        if (startup == StartupType.STEP_INIT) {
        } else if (startup == StartupType.STEP_KERNEL) {
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
}
