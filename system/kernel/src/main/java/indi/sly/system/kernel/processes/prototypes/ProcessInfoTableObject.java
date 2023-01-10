package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessObject;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.values.ProcessInfoEntryDefinition;
import indi.sly.system.kernel.processes.values.ProcessInfoTableDefinition;
import indi.sly.system.kernel.processes.values.ProcessStatusType;
import indi.sly.system.kernel.processes.values.ProcessTokenLimitType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessInfoTableObject extends ABytesValueProcessObject<ProcessInfoTableDefinition, ProcessObject> {
    public synchronized Set<UUID> list() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.list();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public synchronized void inherit(UUID index) {
        if (ValueUtil.isAnyNullOrEmpty(index)) {
            throw new ConditionParametersException();
        }

        if (this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();

        if (!process.getID().equals(this.parent.getParentID())) {
            throw new ConditionRefuseException();
        }

        ProcessInfoTableObject processInfoTable = process.getInfoTable();

        try {
            this.lock(LockType.WRITE);
            this.init();
            processInfoTable.lock(LockType.WRITE);
            processInfoTable.init();

            InfoObject info = processInfoTable.getByIndex(index).getInfo();

            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
            TypeObject type = typeManager.get(info.getType());

            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.CAN_BE_INHERITED)) {
                throw new StatusNotSupportedException();
            }

            ProcessInfoEntryDefinition processInfoEntry = processInfoTable.value.getByIndex(index);
            processInfoTable.value.delete(index);
            this.value.add(processInfoEntry);

            processInfoTable.fresh();
            this.fresh();
        } finally {
            processInfoTable.lock(LockType.NONE);
            this.lock(LockType.NONE);
        }
    }

    public synchronized boolean containByIndex(UUID index) {
        if (ValueUtil.isAnyNullOrEmpty(index)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.containByIndex(index);
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public synchronized boolean containByID(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.containByID(id);
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public synchronized ProcessInfoEntryObject getByIndex(UUID index) {
        if (ValueUtil.isAnyNullOrEmpty(index)) {
            throw new ConditionParametersException();
        }

        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessInfoEntryObject processInfoEntry = this.factoryManager.create(ProcessInfoEntryObject.class);

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (!this.containByIndex(index)) {
                throw new StatusNotExistedException();
            }

            processInfoEntry.setParent(this);
            processInfoEntry.setSource(() -> this.value, (ProcessInfoTableDefinition source) -> {
            });
            processInfoEntry.setLock(this::lock);
            processInfoEntry.index = index;
            processInfoEntry.isProcessCurrent = () -> this.parent.isCurrent();

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        return processInfoEntry;
    }

    public synchronized ProcessInfoEntryObject getByID(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessInfoEntryObject processInfoEntry = this.factoryManager.create(ProcessInfoEntryObject.class);

        try {
            this.lock(LockType.WRITE);
            this.init();

            UUID index = this.value.getByID(id).getIndex();

            processInfoEntry.setParent(this);
            processInfoEntry.setSource(() -> this.value, (ProcessInfoTableDefinition source) -> {
            });
            processInfoEntry.index = index;
            processInfoEntry.isProcessCurrent = () -> this.parent.isCurrent();

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        return processInfoEntry;
    }

    public synchronized ProcessInfoEntryObject create(UUID id, InfoStatusDefinition status, long openAttribute) {
        if (ValueUtil.isAnyNullOrEmpty(id) || ObjectUtil.isAnyNull(status) || LogicalUtil.isAllExist(openAttribute, InfoOpenAttributeType.CLOSE)) {
            throw new ConditionParametersException();
        }

        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        UUID index;

        try {
            this.lock(LockType.READ);
            this.init();

            if (this.containByID(id)) {
                throw new StatusAlreadyExistedException();
            }
            if (this.value.size() >= this.parent.getToken().getLimits().get(ProcessTokenLimitType.INDEX_MAX)) {
                throw new StatusInsufficientResourcesException();
            }

            DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            index = UUIDUtil.createRandom();

            ProcessInfoEntryDefinition processInfoEntry = new ProcessInfoEntryDefinition();
            processInfoEntry.setIndex(index);
            processInfoEntry.getDate().put(DateTimeType.CREATE, nowDateTime);
            processInfoEntry.setID(id);
            processInfoEntry.getIdentifications().addAll(status.getIdentifications());

            InfoOpenDefinition infoOpen = new InfoOpenDefinition();
            infoOpen.setAttribute(openAttribute);
            processInfoEntry.setInfoOpen(infoOpen);

            this.value.add(processInfoEntry);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        return this.getByIndex(index);
    }
}
