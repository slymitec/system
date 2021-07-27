package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusInsufficientResourcesException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AValueProcessPrototype;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import indi.sly.system.kernel.processes.values.ProcessTokenLimitType;
import indi.sly.system.kernel.processes.values.ProcessHandleEntryDefinition;
import indi.sly.system.kernel.processes.values.ProcessHandleTableDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessHandleEntryObject extends AValueProcessPrototype<ProcessHandleTableDefinition> {
    protected ProcessTokenObject processToken;
    protected UUID infoID;
    protected InfoStatusDefinition status;

    public synchronized boolean isExist() {
        return this.value.containByInfoID(infoID);
    }

    public synchronized UUID getHandle() {
        if (!this.isExist()) {
            throw new StatusNotExistedException();
        }

        ProcessHandleEntryDefinition processHandleEntry = this.value.getByInfoID(this.infoID);

        return processHandleEntry.getHandle();
    }

    public synchronized InfoOpenDefinition getOpen() {
        if (!this.isExist()) {
            throw new StatusNotExistedException();
        }

        ProcessHandleEntryDefinition processHandleEntry = this.value.getByInfoID(this.infoID);

        return processHandleEntry.getOpen();
    }

    public synchronized UUID add(long openAttribute) {
        if (this.isExist()) {
            throw new StatusAlreadyExistedException();
        }
        if (this.value.size() >= this.processToken.getLimits().get(ProcessTokenLimitType.HANDLE_MAX)) {
            throw new StatusInsufficientResourcesException();
        }

        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        UUID handle = UUIDUtil.createRandom();

        ProcessHandleEntryDefinition processHandleEntry = new ProcessHandleEntryDefinition();
        processHandleEntry.setHandle(handle);
        processHandleEntry.getIdentifications().addAll(status.getIdentifications());

        InfoOpenDefinition infoOpen = new InfoOpenDefinition();
        infoOpen.setAttribute(openAttribute);
        processHandleEntry.setOpen(infoOpen);

        processHandleEntry.getDate().put(DateTimeType.CREATE, nowDateTime);

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.add(processHandleEntry);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        return handle;
    }

    public synchronized void delete() {
        if (!this.isExist()) {
            throw new StatusNotExistedException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            ProcessHandleEntryDefinition processHandleEntry = this.value.getByInfoID(this.infoID);
            processHandleEntry.getOpen().setAttribute(InfoOpenAttributeType.CLOSE);

            this.value.delete(processHandleEntry.getHandle());

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
