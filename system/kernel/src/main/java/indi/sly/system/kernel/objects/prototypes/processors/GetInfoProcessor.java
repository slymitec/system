package indi.sly.system.kernel.objects.prototypes.processors;

import java.util.UUID;

import javax.inject.Named;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import indi.sly.system.common.functions.Function3;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.prototypes.InfoObjectProcessorRegister;
import indi.sly.system.kernel.objects.prototypes.InfoObjectStatusDefinition;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GetInfoProcessor extends ACoreObject implements IInfoObjectProcessor {
    public GetInfoProcessor() {
        this.info = (repositoryID, id, status) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);

            AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(repositoryID);

            return infoRepository.get(id);
        };
    }

    private final Function3<InfoEntity, UUID, UUID, InfoObjectStatusDefinition> info;

    @Override
    public void process(InfoEntity info, InfoObjectProcessorRegister processorRegister) {
        processorRegister.setInfo(this.info);
    }
}
