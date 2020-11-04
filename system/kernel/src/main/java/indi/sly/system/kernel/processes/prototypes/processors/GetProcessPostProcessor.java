package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.functions.Function;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.entities.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.ProcessObjectProcessorRegister;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GetProcessPostProcessor extends ACoreObject implements IProcessObjectProcessor {
    private final Function<ProcessEntity, UUID> process;

    public GetProcessPostProcessor() {
        this.process = id -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);

            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            return processRepository.get(id);
        };
    }

    @Override
    public void postProcess(ProcessEntity process, ProcessObjectProcessorRegister processorRegister) {
        processorRegister.setProcess(this.process);
    }
}
