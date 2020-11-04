package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.exceptions.AKernelException;
import indi.sly.system.common.functions.Consumer;
import indi.sly.system.common.functions.Provider;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.ACoreObject;
import indi.sly.system.kernel.core.date.DateTimeObject;
import indi.sly.system.kernel.core.date.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.SpaceTypes;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.ABytesProcessObject;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.prototypes.StatusDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessHandleTableObject extends ABytesProcessObject {
    private ProcessHandleTableDefinition processHandleTable;

    @Override
    protected void read(byte[] source) {
        this.processHandleTable = ObjectUtils.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtils.transferToByteArray(this.processHandleTable);
    }

    public Map<Long, Date> getDate(UUID handle) {
        this.init();

        ProcessHandleEntryDefinition processHandleEntry = this.processHandleTable.get(handle);

        return Collections.unmodifiableMap(processHandleEntry.getDate());
    }

    public InfoObject getInfo(UUID handle) {
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

    public UUID addInfo(StatusDefinition info) {
        return null;
    }

    public void deleteInfo(UUID handle) {
    }
}
