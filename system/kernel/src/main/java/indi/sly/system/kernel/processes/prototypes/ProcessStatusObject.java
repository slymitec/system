package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.lang.StatusIsUsedException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AValueProcessObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.lang.ProcessProcessorReadStatusFunction;
import indi.sly.system.kernel.processes.lang.ProcessProcessorWriteStatusConsumer;
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
public class ProcessStatusObject extends AValueProcessObject<ProcessEntity, ProcessObject> {
    protected ProcessProcessorMediator processorMediator;

    public long get() {
        Long status = ProcessStatusType.NULL;

        Set<ProcessProcessorReadStatusFunction> resolvers = this.processorMediator.getReadProcessStatuses();

        for (ProcessProcessorReadStatusFunction resolver : resolvers) {
            status = resolver.apply(status, this.value);
        }

        return status;
    }

    public void initialize() {
        if (this.parent.isCurrent()) {
            throw new ConditionRefuseException();
        }
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.NULL)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();

        if (!currentProcess.getID().equals(this.parent.getParentID())) {
            throw new ConditionRefuseException();
        }

        this.init();

        Set<ProcessProcessorWriteStatusConsumer> resolvers = this.processorMediator.getWriteProcessStatuses();

        for (ProcessProcessorWriteStatusConsumer resolver : resolvers) {
            resolver.accept(this.value, ProcessStatusType.INITIALIZATION);
        }

        ProcessStatisticsObject processStatus = this.parent.getStatistics();
        processStatus.addStatusCumulation(1);
    }

    public void run() {
        if (!this.parent.isCurrent()) {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject currentProcess = processManager.getCurrent();
            ProcessSessionObject currentProcessSession = currentProcess.getSession();
            ProcessTokenObject currentProcessToken = currentProcess.getToken();

            ProcessSessionObject processSession = this.parent.getSession();
            ProcessTokenObject processToken = this.parent.getToken();

            if (!currentProcess.getID().equals(this.parent.getParentID())
                    && !currentProcessToken.isPrivileges(PrivilegeType.SECURITY_DO_WITH_ANY_ACCOUNT)
                    && currentProcessToken.getAccountID() != processToken.getAccountID()
                    && currentProcessSession.getID() != processSession.getID()) {
                throw new ConditionRefuseException();
            }
        }
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.INTERRUPTED)) {
            throw new StatusRelationshipErrorException();
        }

        this.init();

        Set<ProcessProcessorWriteStatusConsumer> resolvers = this.processorMediator.getWriteProcessStatuses();

        for (ProcessProcessorWriteStatusConsumer resolver : resolvers) {
            resolver.accept(this.value, ProcessStatusType.RUNNING);
        }
    }

    public void interrupt() {
        if (!this.parent.isCurrent()) {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject currentProcess = processManager.getCurrent();
            ProcessSessionObject currentProcessSession = currentProcess.getSession();
            ProcessTokenObject currentProcessToken = currentProcess.getToken();

            ProcessSessionObject processSession = this.parent.getSession();
            ProcessTokenObject processToken = this.parent.getToken();

            if (!currentProcessToken.isPrivileges(PrivilegeType.SECURITY_DO_WITH_ANY_ACCOUNT)
                    && currentProcessToken.getAccountID() != processToken.getAccountID()
                    && currentProcessSession.getID() != processSession.getID()) {
                throw new ConditionRefuseException();
            }
        }
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Set<ProcessProcessorWriteStatusConsumer> resolvers = this.processorMediator.getWriteProcessStatuses();

            for (ProcessProcessorWriteStatusConsumer resolver : resolvers) {
                resolver.accept(this.value, ProcessStatusType.INTERRUPTED);
            }

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void die() {
        if (!this.parent.isCurrent()) {
            throw new ConditionRefuseException();
        }
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING,
                ProcessStatusType.INTERRUPTED, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Set<ProcessProcessorWriteStatusConsumer> resolvers = this.processorMediator.getWriteProcessStatuses();

            for (ProcessProcessorWriteStatusConsumer resolver : resolvers) {
                resolver.accept(this.value, ProcessStatusType.DIED);
            }

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void zombie() {
        if (!this.parent.isCurrent()) {
            throw new ConditionRefuseException();
        }
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessCommunicationObject processCommunication = this.parent.getCommunication();
        ProcessInfoTableObject processInfoTable = this.parent.getInfoTable();
        if (!processCommunication.getPortIDs().isEmpty() || !ValueUtil.isAnyNullOrEmpty(processCommunication.getSignalID())
                || !processInfoTable.list().isEmpty()) {
            throw new StatusIsUsedException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Set<ProcessProcessorWriteStatusConsumer> resolvers = this.processorMediator.getWriteProcessStatuses();

            for (ProcessProcessorWriteStatusConsumer resolver : resolvers) {
                resolver.accept(this.value, ProcessStatusType.ZOMBIE);
            }

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
