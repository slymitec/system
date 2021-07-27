package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.kernel.core.prototypes.AValueProcessPrototype;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.lang.ReadProcessStatusFunction;
import indi.sly.system.kernel.processes.lang.WriteProcessStatusConsumer;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.values.ProcessStatusType;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessStatusObject extends AValueProcessPrototype<ProcessEntity> {
    protected ProcessProcessorMediator processorMediator;

    protected ProcessObject process;

    public long get() {
        Long status = ProcessStatusType.NULL;

        Set<ReadProcessStatusFunction> funcs = this.processorMediator.getReadProcessStatuses();

        for (ReadProcessStatusFunction pair : funcs) {
            status = pair.apply(status, this.value);
        }

        return status;
    }

    public void initialize() {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.NULL)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject parentProcess = processManager.getCurrent();

        this.init();

        if (!parentProcess.getID().equals(this.value.getParentProcessID())) {
            throw new ConditionPermissionsException();
        }

        Set<WriteProcessStatusConsumer> funcs = this.processorMediator.getWriteProcessStatuses();

        for (WriteProcessStatusConsumer pair : funcs) {
            pair.accept(this.value, ProcessStatusType.INITIALIZATION);
        }
    }

    public void run() {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.INTERRUPTED)) {
            throw new StatusRelationshipErrorException();
        }

        this.init();

        Set<WriteProcessStatusConsumer> funcs = this.processorMediator.getWriteProcessStatuses();

        for (WriteProcessStatusConsumer pair : funcs) {
            pair.accept(this.value, ProcessStatusType.RUNNING);
        }
    }

    public void interrupt() {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        this.init();

        Set<WriteProcessStatusConsumer> funcs = this.processorMediator.getWriteProcessStatuses();

        for (WriteProcessStatusConsumer pair : funcs) {
            pair.accept(this.value, ProcessStatusType.INTERRUPTED);
        }
    }

    public void die() {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.RUNNING,
                ProcessStatusType.INTERRUPTED, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Set<WriteProcessStatusConsumer> funcs = this.processorMediator.getWriteProcessStatuses();

            for (WriteProcessStatusConsumer pair : funcs) {
                pair.accept(this.value, ProcessStatusType.DIED);
            }

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_ANY_PROCESSES)) {
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
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionPermissionsException();
            }
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Set<WriteProcessStatusConsumer> funcs = this.processorMediator.getWriteProcessStatuses();

            for (WriteProcessStatusConsumer pair : funcs) {
                pair.accept(this.value, ProcessStatusType.ZOMBIE);
            }

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();
        processRepository.delete(this.value);
    }
}
