package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.exceptions.StatusNotExistedException;
import indi.sly.system.common.functions.Function;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.ACoreObject;
import indi.sly.system.kernel.core.enviroment.SpaceTypes;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.caches.InfoObjectCacheObject;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.prototypes.InfoObjectProcessorRegister;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GetParentPostProcessor extends ACoreObject implements IKernelObjectPostProcessor {
    public GetParentPostProcessor() {
        this.parent = (id) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);

            if (UUIDUtils.isAnyNullOrEmpty(id)) {
                throw new StatusNotExistedException();
            }

            InfoObjectCacheObject kernelCache = this.factoryManager.getCoreObjectRepository().get(SpaceTypes.KERNEL, InfoObjectCacheObject.class);

            return kernelCache.getIfExisted(SpaceTypes.ALL, id);
        };
    }

    private final Function<InfoObject, UUID> parent;

    @Override
    public void postProcess(InfoEntity info, InfoObjectProcessorRegister processorRegister) {
        processorRegister.setParent(this.parent);
    }

}
