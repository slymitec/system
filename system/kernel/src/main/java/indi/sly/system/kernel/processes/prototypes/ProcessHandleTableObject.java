package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.*;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessPrototype;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.values.ProcessHandleEntryDefinition;
import indi.sly.system.kernel.processes.values.ProcessHandleTableDefinition;
import indi.sly.system.kernel.processes.values.ProcessStatusType;
import indi.sly.system.kernel.processes.values.ProcessTokenLimitType;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessHandleTableObject extends ABytesValueProcessPrototype<ProcessHandleTableDefinition> {
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

    public Map<Long, Long> getDate(UUID handle) {
        this.checkStatusAndCurrentPermission();

        this.init();

        ProcessHandleEntryDefinition processHandleEntry = this.value.getByHandle(handle);

        return CollectionUtil.unmodifiable(processHandleEntry.getDate());
    }

    public synchronized Set<UUID> list() {
        this.checkStatusAndCurrentPermission();

        this.init();

        return this.value.list();
    }

    public synchronized void inherit(UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(handle)) {
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

        ProcessHandleTableObject parentProcessHandleTable = parentProcess.getHandleTable();

        try {
            this.lock(LockType.WRITE);
            this.init();

            parentProcessHandleTable.lock(LockType.WRITE);
            parentProcessHandleTable.init();

            ProcessHandleEntryDefinition processHandleEntry = parentProcessHandleTable.value.getByHandle(handle);
            parentProcessHandleTable.value.delete(handle);
            this.value.add(processHandleEntry);

            parentProcessHandleTable.fresh();
            this.fresh();
        } finally {
            parentProcessHandleTable.lock(LockType.NONE);
            this.lock(LockType.NONE);
        }
    }

    public synchronized boolean containByHandle(UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new ConditionParametersException();
        }

        this.checkStatusAndCurrentPermission();

        this.init();

        return this.value.containByHandle(handle);
    }

    public synchronized boolean containByInfoID(UUID infoID) {
        if (ValueUtil.isAnyNullOrEmpty(infoID)) {
            throw new ConditionParametersException();
        }

        this.checkStatusAndCurrentPermission();

        this.init();

        return this.value.containByInfoID(infoID);
    }


    public synchronized ProcessHandleEntryObject getByHandle(UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new ConditionParametersException();
        }

        this.checkStatusAndCurrentPermission();

        ProcessHandleEntryObject processHandleEntry = this.factoryManager.create(ProcessHandleEntryObject.class);

        try {
            this.lock(LockType.WRITE);
            this.init();

            processHandleEntry.setParent(this);
            processHandleEntry.setSource(() -> this.value, (ProcessHandleTableDefinition source) -> {
            });
            processHandleEntry.setLock(this::lock);
            processHandleEntry.processToken = this.process.getToken();
            processHandleEntry.handle = handle;

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        return processHandleEntry;
    }

    public synchronized ProcessHandleEntryObject getByInfoID(UUID infoID) {
        if (ValueUtil.isAnyNullOrEmpty(infoID)) {
            throw new ConditionParametersException();
        }

        this.checkStatusAndCurrentPermission();

        ProcessHandleEntryObject processHandleEntry = this.factoryManager.create(ProcessHandleEntryObject.class);

        try {
            this.lock(LockType.WRITE);
            this.init();

            UUID handle = this.value.getByInfoID(infoID).getHandle();

            processHandleEntry.setParent(this);
            processHandleEntry.setSource(() -> this.value, (ProcessHandleTableDefinition source) -> {
            });
            processHandleEntry.setLock(this::lock);
            processHandleEntry.processToken = this.process.getToken();
            processHandleEntry.handle = handle;

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        return processHandleEntry;
    }

    public synchronized void add(UUID infoID, InfoStatusDefinition status, long openAttribute) {
        if (ValueUtil.isAnyNullOrEmpty(infoID) || ObjectUtil.isAnyNull(status) || openAttribute == InfoOpenAttributeType.CLOSE) {
            throw new ConditionParametersException();
        }

        this.checkStatusAndCurrentPermission();

        if (this.value.size() >= this.process.getToken().getLimits().get(ProcessTokenLimitType.HANDLE_MAX)) {
            throw new StatusInsufficientResourcesException();
        }

        DateTimeObject dateTime = this.factoryManager.getCorePrototypeRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        UUID handle = UUIDUtil.createRandom();

        ProcessHandleEntryDefinition processHandleEntry = new ProcessHandleEntryDefinition();
        processHandleEntry.setHandle(handle);
        processHandleEntry.getIdentifications().addAll(status.getIdentifications());

        InfoOpenDefinition infoOpen = new InfoOpenDefinition();
        infoOpen.setAttribute(openAttribute);
        processHandleEntry.setInfoOpen(infoOpen);

        processHandleEntry.getDate().put(DateTimeType.CREATE, nowDateTime);

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.add(processHandleEntry);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public synchronized void delete(UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new ConditionParametersException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            ProcessHandleEntryDefinition processHandleEntry = this.value.getByHandle(handle);
            processHandleEntry.getInfoOpen().setAttribute(InfoOpenAttributeType.CLOSE);

            this.value.delete(processHandleEntry.getHandle());

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
