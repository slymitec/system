package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.lang.StatusInsufficientResourcesException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessObject;
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

import javax.inject.Named;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessInfoTableObject extends ABytesValueProcessObject<ProcessInfoTableDefinition, ProcessObject> {
    public synchronized Set<UUID> list() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING,
                ProcessStatusType.DIED)) {
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

        if (this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
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

        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING,
                ProcessStatusType.DIED)) {
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

        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING,
                ProcessStatusType.DIED)) {
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

        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessInfoEntryObject processInfoEntry = this.factoryManager.create(ProcessInfoEntryObject.class);

        try {
            this.lock(LockType.WRITE);
            this.init();

            processInfoEntry.setParent(this.parent);
            processInfoEntry.setSource(() -> this.value, (ProcessInfoTableDefinition source) -> {
            });
            processInfoEntry.setLock(this::lock);
            processInfoEntry.index = index;

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

        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessInfoEntryObject processInfoEntry = this.factoryManager.create(ProcessInfoEntryObject.class);

        try {
            this.lock(LockType.WRITE);
            this.init();

            UUID index = this.value.getByID(id).getIndex();

            processInfoEntry.setParent(this.parent);
            processInfoEntry.setSource(() -> this.value, (ProcessInfoTableDefinition source) -> {
            });
            processInfoEntry.index = index;

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

        if (!this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        if (this.value.size() >= this.parent.getToken().getLimits().get(ProcessTokenLimitType.INDEX_MAX)) {
            throw new StatusInsufficientResourcesException();
        }

        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        UUID index = UUIDUtil.createRandom();

        ProcessInfoEntryDefinition processInfoEntry = new ProcessInfoEntryDefinition();
        processInfoEntry.setIndex(index);
        processInfoEntry.getIdentifications().addAll(status.getIdentifications());

        InfoOpenDefinition infoOpen = new InfoOpenDefinition();
        infoOpen.setAttribute(openAttribute);
        processInfoEntry.setInfoOpen(infoOpen);

        processInfoEntry.getDate().put(DateTimeType.CREATE, nowDateTime);

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.add(processInfoEntry);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        return this.getByIndex(index);
    }
}
