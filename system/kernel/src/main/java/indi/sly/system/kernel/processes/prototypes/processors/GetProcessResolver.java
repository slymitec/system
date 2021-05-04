package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.lang.Function1;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GetProcessResolver extends APrototype implements IProcessResolver {
    private final Function1<ProcessEntity, UUID> process;

    public GetProcessResolver() {
        this.process = id -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);

            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            return processRepository.get(id);
        };
    }

    @Override
    public void resolve(ProcessEntity process, ProcessProcessorMediator processorMediator) {
        processorMediator.setProcess(this.process);
    }
}
