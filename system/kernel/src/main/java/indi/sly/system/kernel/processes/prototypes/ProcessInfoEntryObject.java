package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.values.MethodScopeType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AValueProcessObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import indi.sly.system.kernel.processes.values.ProcessInfoEntryDefinition;
import indi.sly.system.kernel.processes.values.ProcessInfoTableDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessInfoEntryObject extends AValueProcessObject<ProcessInfoTableDefinition, ProcessInfoTableObject> {
    protected Provider<Boolean> isProcessCurrent;
    protected UUID index;

    private boolean isExist() {
        if (ValueUtil.isAnyNullOrEmpty(this.index)) {
            return false;
        } else {
            return this.value.containByIndex(index);
        }
    }

    public synchronized UUID getIndex() {
        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (!this.isExist()) {
                throw new StatusNotExistedException();
            }

            ProcessInfoEntryDefinition processInfoEntry = this.value.getByIndex(this.index);
            processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        return this.index;
    }

    public synchronized Map<Long, Long> getDate() {
        try {
            this.lock(LockType.READ);
            this.init();

            if (!this.isExist()) {
                throw new StatusNotExistedException();
            }

            ProcessInfoEntryDefinition processInfoEntry = this.value.getByIndex(this.index);
            Map<Long, Long> processInfoEntryDate = processInfoEntry.getDate();

            return CollectionUtil.unmodifiable(processInfoEntryDate);
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public synchronized List<IdentificationDefinition> getIdentifications() {
        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        List<IdentificationDefinition> identifications;

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (!this.isExist()) {
                throw new StatusNotExistedException();
            }

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
        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        InfoOpenDefinition infoOpen;

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (!this.isExist()) {
                throw new StatusNotExistedException();
            }

            ProcessInfoEntryDefinition processInfoEntry = this.value.getByIndex(this.index);
            processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            infoOpen = processInfoEntry.getInfoOpen();

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        return infoOpen;
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public synchronized boolean isUnsupportedDelete() {
        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        boolean unsupportedDelete;

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (!this.isExist()) {
                throw new StatusNotExistedException();
            }

            ProcessInfoEntryDefinition processInfoEntry = this.value.getByIndex(this.index);
            processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            unsupportedDelete = processInfoEntry.isUnsupportedDelete();

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        return unsupportedDelete;
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public synchronized void setUnsupportedDelete(boolean unsupportedDelete) {
        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (!this.isExist()) {
                throw new StatusNotExistedException();
            }

            ProcessInfoEntryDefinition processInfoEntry = this.value.getByIndex(this.index);
            processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            processInfoEntry.setUnsupportedDelete(unsupportedDelete);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public synchronized InfoObject getInfo() {
        if (!this.isProcessCurrent.acquire()) {
            throw new StatusRelationshipErrorException();
        }

        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        List<IdentificationDefinition> identifications;

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (!this.isExist()) {
                throw new StatusNotExistedException();
            }

            ProcessInfoEntryDefinition processInfoEntry = this.value.getByIndex(this.index);
            processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            identifications = processInfoEntry.getIdentifications();

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        InfoObject info = objectManager.get(identifications);

        return info;
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public synchronized void delete() {
        try {
            this.lock(LockType.WRITE);
            this.init();

            if (!this.isExist()) {
                throw new StatusAlreadyFinishedException();
            }

            ProcessInfoEntryDefinition processInfoEntry = this.value.getByIndex(index);

            if (processInfoEntry.isUnsupportedDelete()) {
                throw new StatusDisabilityException();
            }

            processInfoEntry.getInfoOpen().setAttribute(InfoOpenAttributeType.CLOSE);

            this.value.delete(processInfoEntry.getIndex());

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        this.index = null;
    }
}
