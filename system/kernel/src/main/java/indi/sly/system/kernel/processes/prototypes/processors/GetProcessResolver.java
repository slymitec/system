package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.lang.ProcessSelfFunction;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GetProcessResolver extends APrototype implements IProcessResolver {
    private final ProcessSelfFunction process;

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
