package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AValueProcessPrototype;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import indi.sly.system.kernel.processes.values.ProcessHandleEntryDefinition;
import indi.sly.system.kernel.processes.values.ProcessHandleTableDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessHandleEntryObject extends AValueProcessPrototype<ProcessHandleTableDefinition> {
    protected ProcessTokenObject processToken;
    protected UUID handle;

    private boolean isExist() {
        this.init();

        if (ValueUtil.isAnyNullOrEmpty(this.handle)) {
            return false;
        } else {
            return this.value.containByHandle(handle);
        }
    }

    public synchronized UUID getHandle() {
        if (!this.isExist()) {
            throw new StatusNotExistedException();
        }

        DateTimeObject dateTime = this.factoryManager.getCorePrototypeRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        try {
            this.lock(LockType.WRITE);
            this.init();

            ProcessHandleEntryDefinition processHandleEntry = this.value.getByHandle(this.handle);
            processHandleEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        return this.handle;
    }

    public synchronized Map<Long, Long> getDate() {
        if (!this.isExist()) {
            throw new StatusNotExistedException();
        }

        ProcessHandleEntryDefinition processHandleEntry = this.value.getByHandle(this.handle);
        Map<Long, Long> processHandleEntryDate = processHandleEntry.getDate();

        return CollectionUtil.unmodifiable(processHandleEntryDate);
    }

    public synchronized List<IdentificationDefinition> getIdentifications() {
        if (!this.isExist()) {
            throw new StatusNotExistedException();
        }

        DateTimeObject dateTime = this.factoryManager.getCorePrototypeRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        List<IdentificationDefinition> identifications;

        try {
            this.lock(LockType.WRITE);
            this.init();

            ProcessHandleEntryDefinition processHandleEntry = this.value.getByHandle(this.handle);
            processHandleEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            identifications = processHandleEntry.getIdentifications();

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        return CollectionUtil.unmodifiable(identifications);
    }

    public synchronized InfoOpenDefinition getOpen() {
        if (!this.isExist()) {
            throw new StatusNotExistedException();
        }

        DateTimeObject dateTime = this.factoryManager.getCorePrototypeRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        InfoOpenDefinition infoOpen;

        try {
            this.lock(LockType.WRITE);
            this.init();

            ProcessHandleEntryDefinition processHandleEntry = this.value.getByHandle(this.handle);
            processHandleEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            infoOpen = processHandleEntry.getInfoOpen();

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        return infoOpen;
    }

    public synchronized InfoObject getInfo() {
        if (!this.isExist()) {
            throw new StatusNotExistedException();
        }

        DateTimeObject dateTime = this.factoryManager.getCorePrototypeRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        List<IdentificationDefinition> identifications;
        InfoOpenDefinition infoOpen;

        try {
            this.lock(LockType.WRITE);
            this.init();

            ProcessHandleEntryDefinition processHandleEntry = this.value.getByHandle(this.handle);
            processHandleEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            identifications = processHandleEntry.getIdentifications();
            infoOpen = processHandleEntry.getInfoOpen();

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        InfoObject info;
        if (identifications.size() > 0) {
            info = objectManager.get(identifications.subList(0, identifications.size() - 1));
            info = info.rebuildChild(identifications.get(identifications.size() - 1), infoOpen);
        } else {
            info = objectManager.get(identifications);
        }

        return info;
    }
}
