package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AChildCacheableObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.lang.ProcessProcessorReadStatusFunction;
import indi.sly.system.kernel.processes.lang.ProcessProcessorWriteStatusConsumer;
import indi.sly.system.kernel.processes.prototypes.mediators.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessChildCacheEntity;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.values.ProcessStatusType;
import indi.sly.system.kernel.security.values.PrivilegeType;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Set;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessStatusObject extends AChildCacheableObject<ProcessChildCacheEntity, ProcessObject> {
    protected ProcessFactory factory;
    protected ProcessProcessorMediator processorMediator;

    private ProcessEntity getSelf() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getProcess().getProcessId())) {
            throw new ConditionContextException();
        }

        return this.processorMediator.getSelf().apply(this.cache.getProcess().getProcessId());
    }

    public long get() {
        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.READ);
        try {
            Long status = ProcessStatusType.NULL;

            Set<ProcessProcessorReadStatusFunction> resolvers = this.processorMediator.getReadProcessStatuses();

            for (ProcessProcessorReadStatusFunction resolver : resolvers) {
                status = resolver.apply(status, process);
            }

            return status;
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public void initialize() {
        if (this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.NULL)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();

        if (!currentProcess.getId().equals(this.base.getParentId())) {
            throw new ConditionRefuseException();
        }

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            Set<ProcessProcessorWriteStatusConsumer> resolvers = this.processorMediator.getWriteProcessStatuses();

            for (ProcessProcessorWriteStatusConsumer resolver : resolvers) {
                resolver.accept(process, ProcessStatusType.INITIALIZATION);
            }
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }

        ProcessStatisticsObject processStatus = this.base.getStatistics();
        processStatus.addStatusCumulation(1);
    }

    public void run() {
        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.INTERRUPTED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        if (!this.base.isCurrent()) {
            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject currentProcess = processManager.getCurrent();
            ProcessSessionObject currentProcessSession = currentProcess.getSession();
            ProcessTokenObject currentProcessToken = currentProcess.getToken();
            ProcessSessionObject processSession = this.base.getSession();
            ProcessTokenObject processToken = this.base.getToken();

            if (LogicalUtil.isAnyEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
                if (!currentProcess.getId().equals(this.base.getParentId())
                        && !currentProcessToken.isPrivileges(PrivilegeType.SECURITY_DO_WITH_ANY_ACCOUNT)
                        && !currentProcessToken.getAccountId().equals(processToken.getAccountId())
                        && (!ValueUtil.isAnyNullOrEmpty(currentProcessSession.getId()) && !currentProcessSession.getId().equals(processSession.getId()))) {
                    throw new ConditionRefuseException();
                }
            } else if (LogicalUtil.isAnyEqual(this.base.getStatus().get(), ProcessStatusType.INTERRUPTED)) {
                if (!currentProcessToken.isPrivileges(PrivilegeType.SECURITY_DO_WITH_ANY_ACCOUNT)
                        && !currentProcessToken.getAccountId().equals(processToken.getAccountId())
                        && (!ValueUtil.isAnyNullOrEmpty(currentProcessSession.getId()) && !currentProcessSession.getId().equals(processSession.getId()))) {
                    throw new ConditionRefuseException();
                }
            }
        }

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            Set<ProcessProcessorWriteStatusConsumer> resolvers = this.processorMediator.getWriteProcessStatuses();

            for (ProcessProcessorWriteStatusConsumer resolver : resolvers) {
                resolver.accept(process, ProcessStatusType.RUNNING);
            }
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void interrupt() {
        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        if (!this.base.isCurrent()) {
            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject currentProcess = processManager.getCurrent();
            ProcessSessionObject currentProcessSession = currentProcess.getSession();
            ProcessTokenObject currentProcessToken = currentProcess.getToken();
            ProcessSessionObject processSession = this.base.getSession();
            ProcessTokenObject processToken = this.base.getToken();

            if (LogicalUtil.isAnyEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
                if (!currentProcess.getId().equals(this.base.getParentId())
                        && !currentProcessToken.isPrivileges(PrivilegeType.SECURITY_DO_WITH_ANY_ACCOUNT)
                        && !currentProcessToken.getAccountId().equals(processToken.getAccountId())
                        && (!ValueUtil.isAnyNullOrEmpty(currentProcessSession.getId()) && !currentProcessSession.getId().equals(processSession.getId()))) {
                    throw new ConditionRefuseException();
                }
            } else if (LogicalUtil.isAnyEqual(this.base.getStatus().get(), ProcessStatusType.RUNNING)) {
                if (!currentProcessToken.isPrivileges(PrivilegeType.SECURITY_DO_WITH_ANY_ACCOUNT)
                        && !currentProcessToken.getAccountId().equals(processToken.getAccountId())
                        && (!ValueUtil.isAnyNullOrEmpty(currentProcessSession.getId()) && !currentProcessSession.getId().equals(processSession.getId()))) {
                    throw new ConditionRefuseException();
                }
            }
        }

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            Set<ProcessProcessorWriteStatusConsumer> resolvers = this.processorMediator.getWriteProcessStatuses();

            for (ProcessProcessorWriteStatusConsumer resolver : resolvers) {
                resolver.accept(process, ProcessStatusType.INTERRUPTED);
            }
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void die() {
        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING, ProcessStatusType.INTERRUPTED, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            Set<ProcessProcessorWriteStatusConsumer> resolvers = this.processorMediator.getWriteProcessStatuses();

            for (ProcessProcessorWriteStatusConsumer resolver : resolvers) {
                resolver.accept(process, ProcessStatusType.DIED);
            }
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void zombie() {
        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

        ProcessEntity process = this.getSelf();

        ProcessCommunicationObject processCommunication = this.base.getCommunication();
        ProcessInfoTableObject processInfoTable = this.base.getInfoTable();
        ProcessSessionObject processSession = this.base.getSession();

        if (!processCommunication.getPortIds().isEmpty() || processCommunication.isSignalExist()
                || !processInfoTable.list().isEmpty() || !ValueUtil.isAnyNullOrEmpty(processSession.getId())) {
            throw new StatusIsUsedException();
        }

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            Set<ProcessProcessorWriteStatusConsumer> resolvers = this.processorMediator.getWriteProcessStatuses();

            for (ProcessProcessorWriteStatusConsumer resolver : resolvers) {
                resolver.accept(process, ProcessStatusType.ZOMBIE);
            }
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }

        processRepository.delete(processRepository.get(this.base.getId()));
    }
}
