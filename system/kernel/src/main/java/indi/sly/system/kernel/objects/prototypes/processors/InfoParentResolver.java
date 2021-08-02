package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.caches.prototypes.InfoCacheObject;
import indi.sly.system.kernel.objects.lang.InfoProcessorParentFunction;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoParentResolver extends APrototype implements IInfoResolver {
    public InfoParentResolver() {
        this.parent = (id) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);

            if (ValueUtil.isAnyNullOrEmpty(id)) {
                throw new StatusNotExistedException();
            }

            InfoCacheObject infoCache =
                    this.factoryManager.getCoreRepository().get(SpaceType.KERNEL, InfoCacheObject.class);

            return infoCache.getIfExisted(SpaceType.ALL, id);
        };
    }

    private final InfoProcessorParentFunction parent;

    @Override
    public void resolve(InfoEntity info, InfoProcessorMediator processorMediator) {
        processorMediator.setParent(this.parent);
    }

    @Override
    public int order() {
        return 0;
    }
}
