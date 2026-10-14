package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionContextException;
import indi.sly.system.common.lang.StatusDisabilityException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.DateTimeType;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.prototypes.AChildCacheableObject;
import indi.sly.system.kernel.core.values.APersistentEntity;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.objects.values.InfoOpenRecord;
import indi.sly.system.kernel.processes.lang.ProcessProcessorReadComponentFunction;
import indi.sly.system.kernel.processes.lang.ProcessProcessorWriteComponentConsumer;
import indi.sly.system.kernel.processes.prototypes.mediators.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.values.ProcessInfoEntryCacheEntity;
import indi.sly.system.kernel.processes.values.ProcessInfoEntryEntity;
import indi.sly.system.kernel.processes.values.ProcessInfoTableEntity;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessInfoEntryObject extends AChildCacheableObject<ProcessInfoEntryCacheEntity, ProcessInfoTableObject> {
    @SuppressWarnings("NullAway.Init")
    protected ProcessFactory factory;
    @SuppressWarnings("NullAway.Init")
    protected ProcessProcessorMediator processorMediator;

    private ProcessEntity getSelf(long lock) {
        if (ValueUtil.isNullOrEmpty(this.cache.getProcessInfoTable().getProcess().getProcessId())) {
            throw new ConditionContextException();
        }

        return this.processorMediator.getSelf().apply(this.cache.getProcessInfoTable().getProcess().getProcessId(), lock);
    }

    public void initSelf(long lock) {
        this.getSelf(lock);
    }

    private ProcessInfoTableEntity init(ProcessEntity process) {
        Set<ProcessProcessorReadComponentFunction> resolvers = this.processorMediator.getReadProcessInfoTables();

        APersistentEntity source = null;

        for (ProcessProcessorReadComponentFunction resolver : resolvers) {
            source = resolver.apply(source, process);
        }

        if (ObjectUtil.isNull(source)) {
            throw new StatusNotReadyException();
        }

        return (ProcessInfoTableEntity) source;
    }

    private void flush(ProcessEntity process, ProcessInfoTableEntity value) {
        Set<ProcessProcessorWriteComponentConsumer> resolvers = this.processorMediator.getWriteProcessInfoTables();

        for (ProcessProcessorWriteComponentConsumer resolver : resolvers) {
            resolver.accept(process, value);
        }
    }

    private boolean isExist() {
        if (ValueUtil.isNullOrEmpty(this.cache.getIndex())) {
            return false;
        } else {
            return this.base.containByIndex(this.cache.getIndex());
        }
    }

    public UUID getIndex() {
        DateTimeObject dateTime = this.coreManager.getDateTime();
        long nowDateTime = dateTime.getCurrent();

        ProcessEntity process = this.getSelf(LockType.WRITE);

        if (!this.isExist()) {
            throw new StatusNotExistedException();
        }

        ProcessInfoTableEntity processInfoTable = this.init(process);

        ProcessInfoEntryEntity processInfoEntry = processInfoTable.getByIndex(this.cache.getIndex());
        processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

        this.flush(process, processInfoTable);

        return this.cache.getIndex();
    }

    public Map<Long, Long> getDate() {
        ProcessEntity process = this.getSelf(LockType.READ);

        if (!this.isExist()) {
            throw new StatusNotExistedException();
        }

        ProcessInfoTableEntity processInfoTable = this.init(process);

        ProcessInfoEntryEntity processInfoEntry = processInfoTable.getByIndex(this.cache.getIndex());
        Map<Long, Long> processInfoEntryDate = processInfoEntry.getDate();

        return CollectionUtil.unmodifiable(processInfoEntryDate);
    }

    public PathRecord getPath() {
        DateTimeObject dateTime = this.coreManager.getDateTime();
        long nowDateTime = dateTime.getCurrent();

        ProcessEntity process = this.getSelf(LockType.WRITE);

        if (!this.isExist()) {
            throw new StatusNotExistedException();
        }

        ProcessInfoTableEntity processInfoTable = this.init(process);

        ProcessInfoEntryEntity processInfoEntry = processInfoTable.getByIndex(this.cache.getIndex());
        processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

        PathRecord path = processInfoEntry.getPath();

        this.flush(process, processInfoTable);

        return path;
    }

    public InfoOpenRecord getOpen() {
        DateTimeObject dateTime = this.coreManager.getDateTime();
        long nowDateTime = dateTime.getCurrent();

        ProcessEntity process = this.getSelf(LockType.WRITE);

        if (!this.isExist()) {
            throw new StatusNotExistedException();
        }

        ProcessInfoTableEntity processInfoTable = this.init(process);

        ProcessInfoEntryEntity processInfoEntry = processInfoTable.getByIndex(this.cache.getIndex());
        processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

        InfoOpenRecord infoOpen = processInfoEntry.getInfoOpen();

        this.flush(process, processInfoTable);

        return infoOpen;
    }

    public boolean isUnsupportedDelete() {
        DateTimeObject dateTime = this.coreManager.getDateTime();
        long nowDateTime = dateTime.getCurrent();

        ProcessEntity process = this.getSelf(LockType.WRITE);

        if (!this.isExist()) {
            throw new StatusNotExistedException();
        }

        ProcessInfoTableEntity processInfoTable = this.init(process);

        ProcessInfoEntryEntity processInfoEntry = processInfoTable.getByIndex(this.cache.getIndex());
        processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

        boolean unsupportedDelete = processInfoEntry.isUnsupportedDelete();

        this.flush(process, processInfoTable);

        return unsupportedDelete;
    }

    public void setUnsupportedDelete(boolean unsupportedDelete) {
        DateTimeObject dateTime = this.coreManager.getDateTime();
        long nowDateTime = dateTime.getCurrent();

        ProcessEntity process = this.getSelf(LockType.WRITE);

        if (!this.isExist()) {
            throw new StatusNotExistedException();
        }

        ProcessInfoTableEntity processInfoTable = this.init(process);

        ProcessInfoEntryEntity processInfoEntry = processInfoTable.getByIndex(this.cache.getIndex());
        processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

        processInfoEntry.setUnsupportedDelete(unsupportedDelete);

        this.flush(process, processInfoTable);
    }

    public synchronized InfoObject getInfo() {
        DateTimeObject dateTime = this.coreManager.getDateTime();
        long nowDateTime = dateTime.getCurrent();

        ProcessEntity process = this.getSelf(LockType.WRITE);

        if (!this.isExist()) {
            throw new StatusNotExistedException();
        }

        ProcessInfoTableEntity processInfoTable = this.init(process);

        ProcessInfoEntryEntity processInfoEntry = processInfoTable.getByIndex(this.cache.getIndex());
        processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

        PathRecord path = processInfoEntry.getPath();

        this.flush(process, processInfoTable);

        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);

        InfoObject info = objectManager.get(path);

        return info;
    }

    public void delete() {
        ProcessEntity process = this.getSelf(LockType.WRITE);

        if (!this.isExist()) {
            throw new StatusNotExistedException();
        }

        ProcessInfoTableEntity processInfoTable = this.init(process);

        ProcessInfoEntryEntity processInfoEntry = processInfoTable.getByIndex(this.cache.getIndex());

        if (processInfoEntry.isUnsupportedDelete()) {
            throw new StatusDisabilityException();
        }

        processInfoEntry.setInfoOpen(processInfoEntry.getInfoOpen().withAttribute(InfoOpenAttributeType.CLOSE));

        processInfoTable.delete(processInfoEntry.getIndex());

        this.flush(process, processInfoTable);

        this.cache.setIndex(UUIDUtil.getEmpty());
    }
}
