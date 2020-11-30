package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.exceptions.AKernelException;
import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.common.exceptions.StatusRelationshipErrorException;
import indi.sly.system.common.functions.Consumer2;
import indi.sly.system.common.functions.Function2;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.LogicalUtils;
import indi.sly.system.kernel.core.prototypes.AValueProcessObject2;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.communication.prototypes.ProcessCommunicationObject;
import indi.sly.system.kernel.processes.entities.ProcessEntity;
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
public class ProcessStatusObject extends AValueProcessObject2<ProcessEntity, ProcessObject> {
    protected ProcessObjectProcessorRegister processorRegister;

    public long get() {
        Long status = ProcessStatusTypes.NULL;

        List<Function2<Long, Long, ProcessEntity>> funcs = this.processorRegister.getReadProcessStatuses();

        for (Function2<Long, Long, ProcessEntity> pair : funcs) {
            status = pair.apply(status, this.value1);
        }

        return status;
    }

    public void initialize() {
        this.init();

        if (LogicalUtils.allNotEqual(this.value2.getStatus().get(), ProcessStatusTypes.NULL)
                || this.value2.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject parentProcess = processManager.getCurrentProcess();

        if (!parentProcess.getID().equals(this.value1.getParentProcessID())) {
            throw new ConditionPermissionsException();
        }

        List<Consumer2<ProcessEntity, Long>> funcs = this.processorRegister.getWriteProcessStatuses();

        for (Consumer2<ProcessEntity, Long> pair : funcs) {
            pair.accept(this.value1, ProcessStatusTypes.INITIALIZATION);
        }
    }

    public void run() {
        this.init();

        if (LogicalUtils.allNotEqual(this.value2.getStatus().get(), ProcessStatusTypes.INITIALIZATION,
                ProcessStatusTypes.INTERRUPTED)) {
            throw new StatusRelationshipErrorException();
        }

        List<Consumer2<ProcessEntity, Long>> funcs = this.processorRegister.getWriteProcessStatuses();

        for (Consumer2<ProcessEntity, Long> pair : funcs) {
            pair.accept(this.value1, ProcessStatusTypes.RUNNING);
        }
    }

    public void interrupt() {
        this.init();

        if (LogicalUtils.allNotEqual(this.value2.getStatus().get(), ProcessStatusTypes.INITIALIZATION,
                ProcessStatusTypes.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        List<Consumer2<ProcessEntity, Long>> funcs = this.processorRegister.getWriteProcessStatuses();

        for (Consumer2<ProcessEntity, Long> pair : funcs) {
            pair.accept(this.value1, ProcessStatusTypes.INTERRUPTED);
        }
    }

    public void die() {
        ProcessCommunicationObject processCommunication = null;
        ProcessHandleTableObject handleTable = null;

        try {
            this.lock(LockTypes.WRITE);
            this.init();

            if (LogicalUtils.allNotEqual(this.value2.getStatus().get(), ProcessStatusTypes.RUNNING,
                    ProcessStatusTypes.INTERRUPTED, ProcessStatusTypes.DIED)) {
                throw new StatusRelationshipErrorException();
            }

            List<Consumer2<ProcessEntity, Long>> funcs = this.processorRegister.getWriteProcessStatuses();

            for (Consumer2<ProcessEntity, Long> pair : funcs) {
                pair.accept(this.value1, ProcessStatusTypes.DIED);
            }

            processCommunication = this.value2.getCommunication();
            handleTable = this.value2.getHandleTable();

            this.fresh();
        } catch (AKernelException exception) {
            throw exception;
        } finally {
            this.lock(LockTypes.NONE);
        }

        if (!this.value2.isCurrent()) {
            ProcessTokenObject processToken = this.value2.getToken();

            if (!processToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                return;
            }
        }

        processCommunication.deleteAllPort();
        processCommunication.deleteSignal();

        Set<UUID> handles = handleTable.list();
        for (UUID handle : handles) {
            handleTable.getInfo(handle).close();
        }
    }

    public void zombie() {
        try {
            this.lock(LockTypes.WRITE);
            this.init();

            if (LogicalUtils.allNotEqual(this.value2.getStatus().get(), ProcessStatusTypes.DIED)) {
                throw new StatusRelationshipErrorException();
            }

            if (!this.value2.isCurrent()) {
                ProcessTokenObject processToken = this.value2.getToken();

                if (!processToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
                    throw new ConditionPermissionsException();
                }
            }

            List<Consumer2<ProcessEntity, Long>> funcs = this.processorRegister.getWriteProcessStatuses();

            for (Consumer2<ProcessEntity, Long> pair : funcs) {
                pair.accept(this.value1, ProcessStatusTypes.ZOMBIE);
            }

            this.fresh();
        } catch (AKernelException exception) {
            throw exception;
        } finally {
            this.lock(LockTypes.NONE);
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();
        processRepository.delete(this.value1);
    }
}
