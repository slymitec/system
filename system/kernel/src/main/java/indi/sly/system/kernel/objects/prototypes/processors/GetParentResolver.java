package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.caches.prototypes.InfoCacheObject;
import indi.sly.system.kernel.objects.lang.ParentFunction;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GetParentResolver extends APrototype implements IInfoResolver {
    public GetParentResolver() {
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

    private final ParentFunction parent;

    @Override
    public void process(InfoEntity info, InfoProcessorMediator processorRegister) {
        processorRegister.setParent(this.parent);
    }
}
