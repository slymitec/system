package indi.sly.system.kernel.objects.prototypes.processors;

import java.util.UUID;

import javax.inject.Named;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import indi.sly.system.common.functions.Function3;
import indi.sly.system.kernel.core.ACoreObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.prototypes.InfoObjectProcessorRegister;
import indi.sly.system.kernel.objects.prototypes.StatusDefinition;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GetInfoPostProcessor extends ACoreObject implements IKernelObjectPostProcessor {
    public GetInfoPostProcessor() {
        this.info = (repositoryID, id, status) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);

            AInfoRepositoryObject entityRepository = memoryManager.getInfoRepository(repositoryID);

            return entityRepository.get(id);
        };
    }

    private final Function3<InfoEntity, UUID, UUID, StatusDefinition> info;

    @Override
    public void postProcess(InfoEntity info, InfoObjectProcessorRegister processorRegister) {
        processorRegister.setInfo(this.info);
    }
}
