package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.exceptions.*;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.LogicalUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.types.SpaceTypes;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessPrototype;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.values.ProcessHandleEntryDefinition;
import indi.sly.system.kernel.processes.values.ProcessHandleTableDefinition;
import indi.sly.system.kernel.processes.types.ProcessStatusTypes;
import indi.sly.system.kernel.processes.types.ProcessTokenLimitTypes;
import indi.sly.system.kernel.security.types.PrivilegeTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessHandleTableObject extends ABytesValueProcessPrototype<ProcessHandleTableDefinition> {
    private ProcessObject process;

    public void setProcess(ProcessObject process) {
        this.process = process;
    }

    private void checkStatusAndCurrentPermission() {
        if (LogicalUtils.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.RUNNING,
                ProcessStatusTypes.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        if (!this.process.isCurrent()) {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessObject currentProcess = processManager.getCurrentProcess();
            ProcessTokenObject currentProcessToken = currentProcess.getToken();

            if (!currentProcessToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_ANY_PROCESSES)) {
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

    public synchronized void inheritHandle(UUID handle) {
        if (UUIDUtils.isAnyNullOrEmpty(handle)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtils.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject parentProcess = processManager.getCurrentProcess();

        if (!parentProcess.getID().equals(this.process.getParentProcessID())) {
            throw new ConditionPermissionsException();
        }

        ProcessHandleTableObject parentProcessHandleTable = parentProcess.getHandleTable();

        try {
            this.lock(LockTypes.WRITE);
            this.init();
            parentProcessHandleTable.lock(LockTypes.WRITE);
            parentProcessHandleTable.init();

            ProcessHandleEntryDefinition processHandleEntry = parentProcessHandleTable.value.get(handle);
            parentProcessHandleTable.value.delete(handle);
            this.value.add(handle, processHandleEntry);

            parentProcessHandleTable.fresh();
            this.fresh();
        } catch (AKernelException exception) {
            throw exception;
        } finally {
            parentProcessHandleTable.lock(LockTypes.NONE);
            this.lock(LockTypes.NONE);
        }
    }

    public synchronized InfoObject getInfo(UUID handle) {
        this.checkStatusAndCurrentPermission();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceTypes.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        InfoObject info = null;
        try {
            this.lock(LockTypes.WRITE);
            this.init();

            ProcessHandleEntryDefinition processHandleEntry = this.value.get(handle);
            processHandleEntry.getDate().put(DateTimeTypes.ACCESS, nowDateTime);
            info = objectManager.rebuild(processHandleEntry.getIdentifications(), processHandleEntry.getOpen());

            this.fresh();
        } catch (AKernelException exception) {
            throw exception;
        } finally {
            this.lock(LockTypes.NONE);
        }

        return info;
    }

    public synchronized UUID addInfo(InfoStatusDefinition status) {
        this.checkStatusAndCurrentPermission();

        ProcessTokenObject processToken = this.process.getToken();
        if (this.value.size() >= processToken.getLimits().get(ProcessTokenLimitTypes.HANDLE_MAX)) {
            throw new StatusInsufficientResourcesException();
        }

        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceTypes.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        UUID handle = UUIDUtils.createRandom();

        ProcessHandleEntryDefinition processHandleEntry = new ProcessHandleEntryDefinition();
        processHandleEntry.getIdentifications().addAll(status.getIdentifications());
        processHandleEntry.setOpen(status.getOpen());
        processHandleEntry.getDate().put(DateTimeTypes.CREATE, nowDateTime);

        try {
            this.lock(LockTypes.WRITE);
            this.init();

            this.value.add(handle, processHandleEntry);

            this.fresh();
        } catch (AKernelException exception) {
            throw exception;
        } finally {
            this.lock(LockTypes.NONE);
        }

        return handle;
    }

    public synchronized void deleteInfo(UUID handle) {
        this.checkStatusAndCurrentPermission();

        try {
            this.lock(LockTypes.WRITE);
            this.init();

            this.value.delete(handle);

            this.fresh();
        } catch (AKernelException exception) {
            throw exception;
        } finally {
            this.lock(LockTypes.NONE);
        }
    }
}
