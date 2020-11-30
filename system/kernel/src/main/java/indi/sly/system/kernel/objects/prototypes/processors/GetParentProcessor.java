package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.exceptions.StatusNotExistedException;
import indi.sly.system.common.functions.Function;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.core.enviroment.types.SpaceTypes;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.caches.prototypes.InfoObjectCacheObject;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.prototypes.InfoObjectProcessorRegister;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GetParentProcessor extends ACoreObject implements IInfoObjectProcessor {
    public GetParentProcessor() {
        this.parent = (id) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);

            if (UUIDUtils.isAnyNullOrEmpty(id)) {
                throw new StatusNotExistedException();
            }

            InfoObjectCacheObject infoObjectCache =
                    this.factoryManager.getCoreObjectRepository().get(SpaceTypes.KERNEL, InfoObjectCacheObject.class);

            return infoObjectCache.getIfExisted(SpaceTypes.ALL, id);
        };
    }

    private final Function<InfoObject, UUID> parent;

    @Override
    public void process(InfoEntity info, InfoObjectProcessorRegister processorRegister) {
        processorRegister.setParent(this.parent);
    }

}
