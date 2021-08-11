package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.lang.ProcessProcessorSelfFunction;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessSelfResolver extends AResolver implements IProcessResolver {
    private final ProcessProcessorSelfFunction self;

    public ProcessSelfResolver() {
        this.self = id -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);

            ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

            return processRepository.get(id);
        };
    }

    @Override
    public void resolve(ProcessEntity process, ProcessProcessorMediator processorMediator) {
        processorMediator.setSelf(this.self);
    }
}
