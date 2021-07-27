package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessPrototype;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.values.ProcessHandleEntryDefinition;
import indi.sly.system.kernel.processes.values.ProcessHandleTableDefinition;
import indi.sly.system.kernel.processes.values.ProcessStatusType;
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
                throw new ConditionPermissionsException();
            }
        }
    }

    public Map<Long, Long> getDate(UUID handle) {
        this.checkStatusAndCurrentPermission();

        this.init();

        ProcessHandleEntryDefinition processHandleEntry = this.value.get(handle);

        return Collections.unmodifiableMap(processHandleEntry.getDate());
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

        if (!parentProcess.getID().equals(this.process.getParentProcessID())) {
            throw new ConditionPermissionsException();
        }

        ProcessHandleTableObject parentProcessHandleTable = parentProcess.getHandleTable();

        try {
            this.lock(LockType.WRITE);
            this.init();
            parentProcessHandleTable.lock(LockType.WRITE);
            parentProcessHandleTable.init();

            ProcessHandleEntryDefinition processHandleEntry = parentProcessHandleTable.value.get(handle);
            parentProcessHandleTable.value.delete(handle);
            this.value.add(processHandleEntry);

            parentProcessHandleTable.fresh();
            this.fresh();
        } finally {
            parentProcessHandleTable.lock(LockType.NONE);
            this.lock(LockType.NONE);
        }
    }

    public synchronized InfoObject getInfo(UUID handle) {
        this.checkStatusAndCurrentPermission();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        InfoObject info;
        try {
            this.lock(LockType.WRITE);
            this.init();

            ProcessHandleEntryDefinition processHandleEntry = this.value.get(handle);
            processHandleEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);
            info = objectManager.rebuild(processHandleEntry);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        return info;
    }

    public ProcessHandleEntryObject getEntry(UUID infoID, InfoStatusDefinition status) {
        if (ObjectUtil.isAnyNull(infoID, status)) {
            throw new ConditionParametersException();
        }

        this.checkStatusAndCurrentPermission();

        ProcessHandleEntryObject processHandleInfo = this.factoryManager.create(ProcessHandleEntryObject.class);

        processHandleInfo.setParent(this);
        processHandleInfo.setSource(() -> this.value, (ProcessHandleTableDefinition source) -> {
        });
        processHandleInfo.setLock(this::lock);
        processHandleInfo.processToken = this.process.getToken();
        processHandleInfo.infoID = infoID;
        processHandleInfo.status = status;

        return processHandleInfo;
    }
}
