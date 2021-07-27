package indi.sly.system.kernel.objects.prototypes.processors;

import javax.inject.Named;

import indi.sly.system.kernel.objects.lang.InfoSelfFunction;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.values.InfoEntity;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GetSelfResolver extends APrototype implements IInfoResolver {
    public GetSelfResolver() {
        this.info = (repositoryID, id, status) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);

            AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(repositoryID);

            return infoRepository.get(id);
        };
    }

    private final InfoSelfFunction info;

    @Override
    public void resolve(InfoEntity info, InfoProcessorMediator processorMediator) {
        processorMediator.setSelf(this.info);
    }
}
