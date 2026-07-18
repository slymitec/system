package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.*;
import indi.sly.system.common.values.DateTimeType;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.prototypes.AChildCacheableObject;
import indi.sly.system.kernel.core.values.APersistentEntity;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoCacheEntity;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.objects.values.InfoOpenRecord;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.lang.ProcessProcessorReadComponentFunction;
import indi.sly.system.kernel.processes.lang.ProcessProcessorWriteComponentConsumer;
import indi.sly.system.kernel.processes.prototypes.mediators.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessInfoTableObject extends AChildCacheableObject<ProcessChildCacheEntity, ProcessObject> {
    protected ProcessFactory factory;
    protected ProcessProcessorMediator processorMediator;

    private ProcessEntity getSelf() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getProcess().getProcessId())) {
            throw new ConditionContextException();
        }

        return this.processorMediator.getSelf().apply(this.cache.getProcess().getProcessId());
    }

    private ProcessInfoTableEntity init(ProcessEntity process) {
        Set<ProcessProcessorReadComponentFunction> resolvers = this.processorMediator.getReadProcessInfoTables();

        APersistentEntity source = null;

        for (ProcessProcessorReadComponentFunction resolver : resolvers) {
            source = resolver.apply(source, process);
        }

        return (ProcessInfoTableEntity) source;
    }

    private void flush(ProcessEntity process, ProcessInfoTableEntity value) {
        Set<ProcessProcessorWriteComponentConsumer> resolvers = this.processorMediator.getWriteProcessInfoTables();

        for (ProcessProcessorWriteComponentConsumer resolver : resolvers) {
            resolver.accept(process, value);
        }
    }

    public Set<UUID> list() {
        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessInfoTableEntity processInfoTable = this.init(process);

            return CollectionUtil.unmodifiable(processInfoTable.list());
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public void inherit(UUID index) {
        if (ValueUtil.isAnyNullOrEmpty(index)) {
            throw new ConditionParametersException();
        }

        if (this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();

        if (!currentProcess.getId().equals(this.base.getParentId())) {
            throw new ConditionRefuseException();
        }

        ProcessInfoTableObject currentProcessInfoTable = currentProcess.getInfoTable();

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            currentProcessInfoTable.factory.lockProcess(currentProcessInfoTable.cache.getProcess(), LockType.WRITE);

            InfoObject info = currentProcessInfoTable.getByIndex(index).getInfo();

            TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
            TypeObject type = typeManager.get(info.getType());

            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.CAN_BE_INHERITED)) {
                throw new StatusNotSupportedException();
            }

            ProcessInfoTableEntity currentProcessInfoTableEntity = currentProcessInfoTable.init(currentProcessInfoTable.getSelf());
            ProcessInfoTableEntity processInfoTable = this.init(process);

            ProcessInfoEntryEntity processInfoEntry = currentProcessInfoTableEntity.getByIndex(index);
            currentProcessInfoTableEntity.delete(index);
            processInfoTable.add(processInfoEntry);

            currentProcessInfoTable.flush(currentProcessInfoTable.getSelf(), currentProcessInfoTableEntity);
            this.flush(process, processInfoTable);
        } finally {
            currentProcessInfoTable.factory.unlockProcess(currentProcessInfoTable.cache.getProcess(), LockType.WRITE);
            this.factory.unlockProcess(currentProcessInfoTable.cache.getProcess(), LockType.WRITE);
        }
    }

    public boolean containByIndex(UUID index) {
        if (ValueUtil.isAnyNullOrEmpty(index)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessInfoTableEntity processInfoTable = this.init(process);

            return processInfoTable.containByIndex(index);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public boolean containById(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.READ);
        try {
            ProcessInfoTableEntity processInfoTable = this.init(process);

            return processInfoTable.containByID(id);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public ProcessInfoEntryObject getByIndex(UUID index) {
        if (ValueUtil.isAnyNullOrEmpty(index)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        if (!this.containByIndex(index)) {
            throw new StatusNotExistedException();
        }

        this.factory.lockProcess(this.cache.getProcess(), LockType.READ);
        try {
            return this.factory.buildProcessInfoEntry(processorMediator, this, index);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public ProcessInfoEntryObject getById(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.READ);
        try {
            ProcessInfoTableEntity processInfoTable = this.init(process);

            UUID index = processInfoTable.getById(id).getIndex();

            return this.factory.buildProcessInfoEntry(processorMediator, this, index);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public ProcessInfoEntryObject create(UUID id, InfoCacheEntity cache, long openAttribute) {
        if (ValueUtil.isAnyNullOrEmpty(id) || ObjectUtil.isAnyNull(cache) || LogicalUtil.isAllExist(openAttribute, InfoOpenAttributeType.CLOSE)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        UUID index;

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            if (this.containById(id)) {
                throw new StatusAlreadyExistedException();
            }

            ProcessInfoTableEntity processInfoTable = this.init(process);

            if (processInfoTable.size() >= this.base.getToken().getLimits().get(ProcessTokenLimitType.INDEX_MAX)) {
                throw new StatusInsufficientResourcesException();
            }

            DateTimeObject dateTime = this.coreManager.getDateTime();
            long nowDateTime = dateTime.getCurrent();

            index = UUIDUtil.createRandom();

            ProcessInfoEntryEntity processInfoEntry = new ProcessInfoEntryEntity();
            processInfoEntry.setIndex(index);
            processInfoEntry.getDate().put(DateTimeType.CREATE, nowDateTime);
            processInfoEntry.setId(id);
            processInfoEntry.setPath(cache.getPath());

            InfoOpenRecord infoOpen = new InfoOpenRecord(openAttribute, null);
            processInfoEntry.setInfoOpen(infoOpen);

            processInfoTable.add(processInfoEntry);

            this.flush(process, processInfoTable);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }

        return this.getByIndex(index);
    }
}
