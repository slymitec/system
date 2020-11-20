package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.exceptions.StatusRelationshipErrorException;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.entities.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.instances.ProcessTypeInitializer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessStatusObject extends ACoreObject {
    private ProcessEntity process;

    public void setPorcess(ProcessEntity process) {
        this.process = process;
    }

    public long get() {
        return this.process.getStatus();
    }

    public void interrupt() {
        if (this.process.getStatus() != ProcessStatusTypes.RUNNING) {
            throw new StatusRelationshipErrorException();
        }

        this.process.setStatus(ProcessStatusTypes.INTERRUPTED);
    }

    public void resume() {
        if (this.process.getStatus() != ProcessStatusTypes.INITIALIZATION
                && this.process.getStatus() != ProcessStatusTypes.INTERRUPTED) {
            throw new StatusRelationshipErrorException();
        }

        this.process.setStatus(ProcessStatusTypes.RUNNING);
    }

    public void stop() {
        this.process.setStatus(ProcessStatusTypes.DIED);

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

        processRepository.delete(this.process);
    }
}
