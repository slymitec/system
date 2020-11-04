package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.exceptions.AKernelException;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.date.DateTimeObject;
import indi.sly.system.kernel.core.date.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.SpaceTypes;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.core.prototypes.ABytesProcessObject;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.prototypes.StatusDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessHandleTableObject extends ABytesProcessObject {
    @Override
    protected void read(byte[] source) {
        this.processHandleTable = ObjectUtils.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtils.transferToByteArray(this.processHandleTable);
    }

    private ProcessHandleTableDefinition processHandleTable;

    public Map<Long, Date> getDate(UUID handle) {
        this.init();

        ProcessHandleEntryDefinition processHandleEntry = this.processHandleTable.get(handle);

        return Collections.unmodifiableMap(processHandleEntry.getDate());
    }

    public synchronized InfoObject getInfo(UUID handle) {
        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().get(SpaceTypes.KERNEL, DateTimeObject.class);
        Date nowDateTime = dateTime.getCurrentDateTime();

        InfoObject info = null;
        try {
            this.lock(LockTypes.WRITE);
            this.init();

            ProcessHandleEntryDefinition processHandleEntry = this.processHandleTable.get(handle);
            processHandleEntry.getDate().put(DateTimeTypes.ACCESS, nowDateTime);
            info = objectManager.rebuild(processHandleEntry.getIdentifications(), processHandleEntry.getOpen());

            this.fresh();
            this.lock(LockTypes.NONE);
        } catch (AKernelException exception) {
            this.lock(LockTypes.NONE);
            throw exception;
        }

        return info;
    }

    public synchronized UUID addInfo(StatusDefinition status) {
        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().get(SpaceTypes.KERNEL, DateTimeObject.class);
        Date nowDateTime = dateTime.getCurrentDateTime();

        UUID handle = UUIDUtils.createRandom();

        ProcessHandleEntryDefinition processHandleEntry = new ProcessHandleEntryDefinition();
        processHandleEntry.getIdentifications().addAll(status.getIdentifications());
        processHandleEntry.setOpen(status.getOpen());
        processHandleEntry.getDate().put(DateTimeTypes.CREATE, nowDateTime);

        this.lock(LockTypes.WRITE);
        this.init();

        this.processHandleTable.add(handle, processHandleEntry);

        this.fresh();
        this.lock(LockTypes.NONE);

        return handle;
    }

    public synchronized void deleteInfo(UUID handle) {
        this.lock(LockTypes.WRITE);
        this.init();

        this.processHandleTable.delete(handle);

        this.fresh();
        this.lock(LockTypes.NONE);
    }
}
