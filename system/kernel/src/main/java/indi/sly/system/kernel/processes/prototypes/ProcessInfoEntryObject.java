package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AIndependentValueProcessObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import indi.sly.system.kernel.processes.values.ProcessInfoEntryDefinition;
import indi.sly.system.kernel.processes.values.ProcessInfoTableDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessInfoEntryObject extends AIndependentValueProcessObject<ProcessInfoTableDefinition> {
    protected ProcessTokenObject processToken;
    protected UUID index;

    private boolean isExist() {
        this.init();

        if (ValueUtil.isAnyNullOrEmpty(this.index)) {
            return false;
        } else {
            return this.value.containByIndex(index);
        }
    }

    public synchronized UUID getIndex() {
        if (!this.isExist()) {
            throw new StatusNotExistedException();
        }

        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        try {
            this.lock(LockType.WRITE);
            this.init();

            ProcessInfoEntryDefinition processInfoEntry = this.value.getByIndex(this.index);
            processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        return this.index;
    }

    public synchronized Map<Long, Long> getDate() {
        if (!this.isExist()) {
            throw new StatusNotExistedException();
        }

        ProcessInfoEntryDefinition processInfoEntry = this.value.getByIndex(this.index);
        Map<Long, Long> processInfoEntryDate = processInfoEntry.getDate();

        return CollectionUtil.unmodifiable(processInfoEntryDate);
    }

    public synchronized List<IdentificationDefinition> getIdentifications() {
        if (!this.isExist()) {
            throw new StatusNotExistedException();
        }

        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        List<IdentificationDefinition> identifications;

        try {
            this.lock(LockType.WRITE);
            this.init();

            ProcessInfoEntryDefinition processInfoEntry = this.value.getByIndex(this.index);
            processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            identifications = processInfoEntry.getIdentifications();

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

        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        InfoOpenDefinition infoOpen;

        try {
            this.lock(LockType.WRITE);
            this.init();

            ProcessInfoEntryDefinition processInfoEntry = this.value.getByIndex(this.index);
            processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            infoOpen = processInfoEntry.getInfoOpen();

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

        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        List<IdentificationDefinition> identifications;
        InfoOpenDefinition infoOpen;

        try {
            this.lock(LockType.WRITE);
            this.init();

            ProcessInfoEntryDefinition processInfoEntry = this.value.getByIndex(this.index);
            processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            identifications = processInfoEntry.getIdentifications();
            infoOpen = processInfoEntry.getInfoOpen();

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
