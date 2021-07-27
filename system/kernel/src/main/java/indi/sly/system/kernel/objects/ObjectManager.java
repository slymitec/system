package indi.sly.system.kernel.objects;

import java.util.List;
import java.util.UUID;

import javax.inject.Named;

import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.memory.caches.prototypes.InfoCacheObject;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import indi.sly.system.kernel.processes.values.ProcessHandleEntryDefinition;
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

    public InfoObject rebuild(ProcessHandleEntryDefinition processHandleEntry) {
        if (ObjectUtil.isAnyNull(processHandleEntry)) {
            throw new ConditionParametersException();
        }

        List<IdentificationDefinition> identifications = processHandleEntry.getIdentifications();
        UUID handle = processHandleEntry.getHandle();
        InfoOpenDefinition infoStatusOpen = processHandleEntry.getOpen();

        InfoObject info;
        if (identifications.size() > 0) {
            info = this.get(identifications.subList(0, identifications.size() - 1));
            info = info.rebuildChild(identifications.get(identifications.size() - 1), handle, infoStatusOpen);
        } else {
            info = this.get(identifications);
        }

        return info;
    }
}
