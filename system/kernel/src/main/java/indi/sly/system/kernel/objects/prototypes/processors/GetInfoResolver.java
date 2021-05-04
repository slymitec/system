package indi.sly.system.kernel.objects.prototypes.processors;

import javax.inject.Named;

import indi.sly.system.kernel.objects.lang.InfoFunction;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.values.InfoEntity;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GetInfoResolver extends APrototype implements IInfoResolver {
    public GetInfoResolver() {
        this.info = (repositoryID, id, status) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);

            AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(repositoryID);

            return infoRepository.get(id);
        };
    }

    private final InfoFunction info;

    @Override
    public void process(InfoEntity info, InfoProcessorMediator processorRegister) {
        processorRegister.setInfo(this.info);
    }
}
