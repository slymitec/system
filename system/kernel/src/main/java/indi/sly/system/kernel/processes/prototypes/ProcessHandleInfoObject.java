package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.StatusInsufficientResourcesException;
import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AValueProcessPrototype;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;
import indi.sly.system.kernel.processes.values.ProcessTokenLimitType;
import indi.sly.system.kernel.processes.values.ProcessHandleEntryDefinition;
import indi.sly.system.kernel.processes.values.ProcessHandleTableDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessHandleInfoObject extends AValueProcessPrototype<ProcessHandleTableDefinition> {
    protected ProcessTokenObject processToken;
    protected InfoStatusDefinition status;

    public synchronized boolean isExist() {
        UUID handle = this.status.getHandle();

        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new StatusNotReadyException();
        }

        this.init();

        return this.value.contain(handle);
    }

    public synchronized UUID add() {
        if (this.value.size() >= this.processToken.getLimits().get(ProcessTokenLimitType.HANDLE_MAX)) {
            throw new StatusInsufficientResourcesException();
        }

        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        UUID handle = UUIDUtil.createRandom();

        ProcessHandleEntryDefinition processHandleEntry = new ProcessHandleEntryDefinition();
        processHandleEntry.getIdentifications().addAll(status.getIdentifications());
        processHandleEntry.setOpen(status.getOpen());
        processHandleEntry.getDate().put(DateTimeType.CREATE, nowDateTime);

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.add(handle, processHandleEntry);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        return handle;
    }

    public synchronized void delete() {
        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.delete(this.status.getHandle());

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
