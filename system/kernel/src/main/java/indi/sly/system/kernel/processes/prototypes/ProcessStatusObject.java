package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.exceptions.StatusRelationshipErrorException;
import indi.sly.system.common.functions.Consumer;
import indi.sly.system.common.functions.Consumer2;
import indi.sly.system.common.functions.Function;
import indi.sly.system.common.functions.Function2;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.entities.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.instances.ProcessTypeInitializer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessStatusObject extends ACoreObject {
    protected ProcessObjectProcessorRegister processorRegister;
    private ProcessEntity process;
    private ProcessObject processObject;

    public void setProcess(ProcessEntity process) {
        this.process = process;
    }

    public void setProcessObject(ProcessObject processObject) {
        this.processObject = processObject;
    }

    public long get() {
        Long status = ProcessStatusTypes.NULL;

        List<Function2<Long, Long, ProcessEntity>> funcs = this.processorRegister.getReadProcessStatuses();

        for (Function2<Long, Long, ProcessEntity> pair : funcs) {
            status = pair.apply(status, this.process);
        }

        return status;
    }

    public void interrupt() {
        if (this.process.getStatus() != ProcessStatusTypes.RUNNING) {
            throw new StatusRelationshipErrorException();
        }

        List<Consumer2<ProcessEntity, Long>> funcs = this.processorRegister.getWriteProcessStatuses();

        for (Consumer2<ProcessEntity, Long> pair : funcs) {
            pair.accept(this.process, ProcessStatusTypes.INTERRUPTED);
        }
    }

    public void resume() {
        if (this.process.getStatus() != ProcessStatusTypes.INITIALIZATION
                && this.process.getStatus() != ProcessStatusTypes.INTERRUPTED) {
            throw new StatusRelationshipErrorException();
        }

        List<Consumer2<ProcessEntity, Long>> funcs = this.processorRegister.getWriteProcessStatuses();

        for (Consumer2<ProcessEntity, Long> pair : funcs) {
            pair.accept(this.process, ProcessStatusTypes.RUNNING);
        }
    }

    public void stop() {
        if (this.process.getStatus() != ProcessStatusTypes.RUNNING
                && this.process.getStatus() != ProcessStatusTypes.INTERRUPTED) {
            throw new StatusRelationshipErrorException();
        }

        List<Consumer2<ProcessEntity, Long>> funcs = this.processorRegister.getWriteProcessStatuses();

        for (Consumer2<ProcessEntity, Long> pair : funcs) {
            pair.accept(this.process, ProcessStatusTypes.DIED);
        }


        //释放资源（跨进程通信、句柄等）

        ProcessHandleTableObject handleTable = this.processObject.getHandleTable();
        Set<UUID> handles = handleTable.list();
        for (UUID handle : handles) {
            handleTable.getInfo(handle).close();
        }

        for (Consumer2<ProcessEntity, Long> pair : funcs) {
            pair.accept(this.process, ProcessStatusTypes.ZOMBIE);
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();
        processRepository.delete(this.process);
    }
}
