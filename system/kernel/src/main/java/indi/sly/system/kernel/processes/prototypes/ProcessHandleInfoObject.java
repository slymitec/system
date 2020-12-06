package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.exceptions.AKernelException;
import indi.sly.system.common.exceptions.StatusInsufficientResourcesException;
import indi.sly.system.common.exceptions.StatusNotReadyException;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.types.SpaceTypes;
import indi.sly.system.kernel.core.prototypes.AValueProcessPrototype;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;
import indi.sly.system.kernel.processes.types.ProcessTokenLimitTypes;
import indi.sly.system.kernel.processes.values.ProcessHandleEntryDefinition;
import indi.sly.system.kernel.processes.values.ProcessHandleTableDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessHandleInfoObject extends AValueProcessPrototype<ProcessHandleTableDefinition> {
    private ProcessTokenObject processToken;
    private InfoStatusDefinition status;

    public void setProcessToken(ProcessTokenObject processToken) {
        this.processToken = processToken;
    }

    public void setStatus(InfoStatusDefinition status) {
        this.status = status;
    }

    public synchronized boolean isExist() {
        UUID handle = this.status.getHandle();

        if (UUIDUtils.isAnyNullOrEmpty(handle)) {
            throw new StatusNotReadyException();
        }

        this.init();

        return this.value.contain(handle);
    }

    public synchronized UUID add() {
        if (this.value.size() >= this.processToken.getLimits().get(ProcessTokenLimitTypes.HANDLE_MAX)) {
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
        } finally {
            this.lock(LockTypes.NONE);
        }

        return handle;
    }

    public synchronized void delete() {
        try {
            this.lock(LockTypes.WRITE);
            this.init();

            this.value.delete(this.status.getHandle());

            this.fresh();
        } finally {
            this.lock(LockTypes.NONE);
        }
    }
}
