package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.*;
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
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessInfoTableObject extends ABytesValueProcessObject<ProcessInfoTableDefinition> {
    protected ProcessObject process;

    private void checkStatusAndCurrentPermission() {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.RUNNING,
                ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        if (!this.process.isCurrent()) {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessObject currentProcess = processManager.getCurrent();
            ProcessTokenObject currentProcessToken = currentProcess.getToken();

            if (!currentProcessToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_ANY_PROCESSES)) {
                throw new ConditionRefuseException();
            }
        }
    }

    public Map<Long, Long> getDate(UUID index) {
        this.checkStatusAndCurrentPermission();

        this.init();

        ProcessInfoEntryDefinition processInfoEntry = this.value.getByIndex(index);

        return CollectionUtil.unmodifiable(processInfoEntry.getDate());
    }

    public synchronized Set<UUID> list() {
        this.checkStatusAndCurrentPermission();

        this.init();

        return this.value.list();
    }

    public synchronized void inherit(UUID index) {
        if (ValueUtil.isAnyNullOrEmpty(index)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject parentProcess = processManager.getCurrent();

        if (!parentProcess.getID().equals(this.process.getParentID())) {
            throw new ConditionRefuseException();
        }

        ProcessInfoTableObject parentProcessInfoTable = parentProcess.getInfoTable();

        try {
            this.lock(LockType.WRITE);
            this.init();

            parentProcessInfoTable.lock(LockType.WRITE);
            parentProcessInfoTable.init();

            ProcessInfoEntryDefinition processInfoEntry = parentProcessInfoTable.value.getByIndex(index);
            parentProcessInfoTable.value.delete(index);
            this.value.add(processInfoEntry);

            parentProcessInfoTable.fresh();
            this.fresh();
        } finally {
            parentProcessInfoTable.lock(LockType.NONE);
            this.lock(LockType.NONE);
        }
    }

    public synchronized boolean containByIndex(UUID index) {
        if (ValueUtil.isAnyNullOrEmpty(index)) {
            throw new ConditionParametersException();
        }

        this.checkStatusAndCurrentPermission();

        this.init();

        return this.value.containByIndex(index);
    }

    public synchronized boolean containByID(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        this.checkStatusAndCurrentPermission();

        this.init();

        return this.value.containByID(id);
    }


    public synchronized ProcessInfoEntryObject getByIndex(UUID index) {
        if (ValueUtil.isAnyNullOrEmpty(index)) {
            throw new ConditionParametersException();
        }

        this.checkStatusAndCurrentPermission();

        ProcessInfoEntryObject processInfoEntry = this.factoryManager.create(ProcessInfoEntryObject.class);

        try {
            this.lock(LockType.WRITE);
            this.init();

            processInfoEntry.setParent(this);
            processInfoEntry.setSource(() -> this.value, (ProcessInfoTableDefinition source) -> {
            });
            processInfoEntry.setLock(this::lock);
            processInfoEntry.processToken = this.process.getToken();
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

        this.checkStatusAndCurrentPermission();

        ProcessInfoEntryObject processInfoEntry = this.factoryManager.create(ProcessInfoEntryObject.class);

        try {
            this.lock(LockType.WRITE);
            this.init();

            UUID index = this.value.getByID(id).getIndex();

            processInfoEntry.setParent(this);
            processInfoEntry.setSource(() -> this.value, (ProcessInfoTableDefinition source) -> {
            });
            processInfoEntry.setLock(this::lock);
            processInfoEntry.processToken = this.process.getToken();
            processInfoEntry.index = index;

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        return processInfoEntry;
    }

    public synchronized void add(UUID id, InfoStatusDefinition status, long openAttribute) {
        if (ValueUtil.isAnyNullOrEmpty(id) || ObjectUtil.isAnyNull(status) || openAttribute == InfoOpenAttributeType.CLOSE) {
            throw new ConditionParametersException();
        }

        this.checkStatusAndCurrentPermission();

        if (this.value.size() >= this.process.getToken().getLimits().get(ProcessTokenLimitType.INDEX_MAX)) {
            throw new StatusInsufficientResourcesException();
        }

        DateTimeObject dateTime = this.factoryManager.getCorePrototypeRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);
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
    }

    public synchronized void delete(UUID index) {
        if (ValueUtil.isAnyNullOrEmpty(index)) {
            throw new ConditionParametersException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            ProcessInfoEntryDefinition processInfoEntry = this.value.getByIndex(index);
            processInfoEntry.getInfoOpen().setAttribute(InfoOpenAttributeType.CLOSE);

            this.value.delete(processInfoEntry.getIndex());

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
