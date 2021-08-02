package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.kernel.core.prototypes.AValueProcessPrototype;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.lang.ProcessProcessorReadProcessStatusFunction;
import indi.sly.system.kernel.processes.lang.ProcessProcessorWriteProcessStatusConsumer;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.values.ProcessStatusType;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Set;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessStatusObject extends AValueProcessPrototype<ProcessEntity> {
    protected ProcessProcessorMediator processorMediator;

    protected ProcessObject process;

    public long get() {
        Long status = ProcessStatusType.NULL;

        Set<ProcessProcessorReadProcessStatusFunction> resolvers = this.processorMediator.getReadProcessStatuses();

        for (ProcessProcessorReadProcessStatusFunction resolver : resolvers) {
            status = resolver.apply(status, this.value);
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

        Set<ProcessProcessorWriteProcessStatusConsumer> resolvers = this.processorMediator.getWriteProcessStatuses();

        for (ProcessProcessorWriteProcessStatusConsumer resolver : resolvers) {
            resolver.accept(this.value, ProcessStatusType.INITIALIZATION);
        }
    }

    public void run() {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.INTERRUPTED)) {
            throw new StatusRelationshipErrorException();
        }

        this.init();

        Set<ProcessProcessorWriteProcessStatusConsumer> resolvers = this.processorMediator.getWriteProcessStatuses();

        for (ProcessProcessorWriteProcessStatusConsumer resolver : resolvers) {
            resolver.accept(this.value, ProcessStatusType.RUNNING);
        }
    }

    public void interrupt() {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        this.init();

        Set<ProcessProcessorWriteProcessStatusConsumer> resolvers = this.processorMediator.getWriteProcessStatuses();

        for (ProcessProcessorWriteProcessStatusConsumer resolver : resolvers) {
            resolver.accept(this.value, ProcessStatusType.INTERRUPTED);
        }
    }

    public void die() {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.RUNNING,
                ProcessStatusType.INTERRUPTED, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        if (!this.process.isCurrent()) {
            ProcessTokenObject processToken = this.process.getToken();

            if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_ANY_PROCESSES)) {
                return;
            }
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Set<ProcessProcessorWriteProcessStatusConsumer> resolvers = this.processorMediator.getWriteProcessStatuses();

            for (ProcessProcessorWriteProcessStatusConsumer resolver : resolvers) {
                resolver.accept(this.value, ProcessStatusType.DIED);
            }

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
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

        ProcessCommunicationObject processCommunication = this.process.getCommunication();
        ProcessHandleTableObject processHandleTable = this.process.getHandleTable();
        if (!processCommunication.getPortIDs().isEmpty() || !ValueUtil.isAnyNullOrEmpty(processCommunication.getSignalID())
                || !processHandleTable.list().isEmpty()) {
            throw new StatusNotReadyException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Set<ProcessProcessorWriteProcessStatusConsumer> resolvers = this.processorMediator.getWriteProcessStatuses();

            for (ProcessProcessorWriteProcessStatusConsumer resolver : resolvers) {
                resolver.accept(this.value, ProcessStatusType.ZOMBIE);
            }

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
