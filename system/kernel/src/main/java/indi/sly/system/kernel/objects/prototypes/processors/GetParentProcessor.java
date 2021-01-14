package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.lang.Function1;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.ACorePrototype;
import indi.sly.system.kernel.core.enviroment.types.SpaceTypes;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.caches.prototypes.InfoCacheObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.prototypes.InfoProcessorRegister;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GetParentProcessor extends ACorePrototype implements IInfoObjectProcessor {
    public GetParentProcessor() {
        this.parent = (id) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);

            if (ValueUtil.isAnyNullOrEmpty(id)) {
                throw new StatusNotExistedException();
            }

            InfoCacheObject infoCache =
                    this.factoryManager.getCoreRepository().get(SpaceTypes.KERNEL, InfoCacheObject.class);

            return infoCache.getIfExisted(SpaceTypes.ALL, id);
        };
    }

    private final Function1<InfoObject, UUID> parent;

    @Override
    public void process(InfoEntity info, InfoProcessorRegister processorRegister) {
        processorRegister.setParent(this.parent);
    }
}
