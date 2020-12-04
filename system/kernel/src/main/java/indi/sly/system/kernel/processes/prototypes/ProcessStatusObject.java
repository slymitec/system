package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.exceptions.AKernelException;
import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.common.exceptions.StatusRelationshipErrorException;
import indi.sly.system.common.functions.Consumer2;
import indi.sly.system.common.functions.Function2;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.LogicalUtils;
import indi.sly.system.kernel.core.prototypes.AValueProcessPrototype;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.communication.prototypes.ProcessCommunicationObject;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.types.ProcessStatusTypes;
import indi.sly.system.kernel.security.types.PrivilegeTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessStatusObject extends AValueProcessPrototype<ProcessEntity> {
    protected ProcessProcessorRegister processorRegister;

    private ProcessObject process;

    public void setProcess(ProcessObject process) {
        this.process = process;
    }

    public long get() {
        Long status = ProcessStatusTypes.NULL;

        List<Function2<Long, Long, ProcessEntity>> funcs = this.processorRegister.getReadProcessStatuses();

        for (Function2<Long, Long, ProcessEntity> pair : funcs) {
            status = pair.apply(status, this.value);
        }

        return status;
    }

    public void initialize() {
        if (LogicalUtils.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.NULL)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject parentProcess = processManager.getCurrentProcess();

        this.init();

        if (!parentProcess.getID().equals(this.value.getParentProcessID())) {
            throw new ConditionPermissionsException();
        }

        List<Consumer2<ProcessEntity, Long>> funcs = this.processorRegister.getWriteProcessStatuses();

        for (Consumer2<ProcessEntity, Long> pair : funcs) {
            pair.accept(this.value, ProcessStatusTypes.INITIALIZATION);
        }
    }

    public void run() {
        if (LogicalUtils.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION,
                ProcessStatusTypes.INTERRUPTED)) {
            throw new StatusRelationshipErrorException();
        }

        this.init();

        List<Consumer2<ProcessEntity, Long>> funcs = this.processorRegister.getWriteProcessStatuses();

        for (Consumer2<ProcessEntity, Long> pair : funcs) {
            pair.accept(this.value, ProcessStatusTypes.RUNNING);
        }
    }

    public void interrupt() {
        if (LogicalUtils.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION,
                ProcessStatusTypes.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        this.init();

        List<Consumer2<ProcessEntity, Long>> funcs = this.processorRegister.getWriteProcessStatuses();

        for (Consumer2<ProcessEntity, Long> pair : funcs) {
            pair.accept(this.value, ProcessStatusTypes.INTERRUPTED);
        }
    }

    public void die() {
        if (LogicalUtils.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.RUNNING,
                ProcessStatusTypes.INTERRUPTED, ProcessStatusTypes.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockTypes.WRITE);
            this.init();

            List<Consumer2<ProcessEntity, Long>> funcs = this.processorRegister.getWriteProcessStatuses();

            for (Consumer2<ProcessEntity, Long> pair : funcs) {
                pair.accept(this.value, ProcessStatusTypes.DIED);
            }

            this.fresh();
        } catch (AKernelException exception) {
            throw exception;
        } finally {
            this.lock(LockTypes.NONE);
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                return;
            }
        }

        ProcessCommunicationObject processCommunication = this.process.getCommunication();
        processCommunication.deleteAllPort();
        processCommunication.deleteSignal();

        ProcessHandleTableObject handleTable = this.process.getHandleTable();
        Set<UUID> handles = handleTable.list();
        for (UUID handle : handles) {
            handleTable.getInfo(handle).close();
        }
    }

    public void zombie() {
        if (LogicalUtils.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        try {
            this.lock(LockTypes.WRITE);
            this.init();

            List<Consumer2<ProcessEntity, Long>> funcs = this.processorRegister.getWriteProcessStatuses();

            for (Consumer2<ProcessEntity, Long> pair : funcs) {
                pair.accept(this.value, ProcessStatusTypes.ZOMBIE);
            }

            this.fresh();
        } catch (AKernelException exception) {
            throw exception;
        } finally {
            this.lock(LockTypes.NONE);
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();
        processRepository.delete(this.value);
    }
}
