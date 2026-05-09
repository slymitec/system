package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.prototypes.AChildCacheableObject;
import indi.sly.system.kernel.core.prototypes.IByteValueProcess;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import indi.sly.system.kernel.processes.lang.ProcessProcessorReadComponentFunction;
import indi.sly.system.kernel.processes.lang.ProcessProcessorWriteComponentConsumer;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessInfoEntryObject extends AChildCacheableObject<ProcessInfoEntryCacheEntity, ProcessInfoTableObject> implements IByteValueProcess<ProcessInfoTableDefinition> {
    protected ProcessFactory factory;
    protected ProcessProcessorMediator processorMediator;

    protected Provider<Boolean> isProcessCurrent;

    private ProcessEntity getSelf() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getProcessInfoTable().getProcess().getProcessId())) {
            throw new ConditionContextException();
        }

        return this.processorMediator.getSelf().apply(this.cache.getProcessInfoTable().getProcess().getProcessId());
    }

    private ProcessInfoTableDefinition init(ProcessEntity process) {
        Set<ProcessProcessorReadComponentFunction> resolvers = this.processorMediator.getReadProcessInfoTables();

        byte[] source = null;

        for (ProcessProcessorReadComponentFunction resolver : resolvers) {
            source = resolver.apply(source, process);
        }

        return IByteValueProcess.super.init(source);
    }

    private void flush(ProcessEntity process, ProcessInfoTableDefinition value) {
        byte[] source = IByteValueProcess.super.flush(value);

        Set<ProcessProcessorWriteComponentConsumer> resolvers = this.processorMediator.getWriteProcessInfoTables();

        for (ProcessProcessorWriteComponentConsumer resolver : resolvers) {
            resolver.accept(process, source);
        }
    }

    private boolean isExist() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getIndex())) {
            return false;
        } else {
            return this.base.containByIndex(this.cache.getIndex());
        }
    }

    public UUID getIndex() {
        DateTimeObject dateTime = this.coreManager.getDateTime();
        long nowDateTime = dateTime.getCurrentDateTime();

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcessInfoTable().getProcess(), LockType.WRITE);

            if (!this.isExist()) {
                throw new StatusNotExistedException();
            }

            ProcessInfoTableDefinition processInfoTable = this.init(process);

            ProcessInfoEntryDefinition processInfoEntry = processInfoTable.getByIndex(this.cache.getIndex());
            processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            this.flush(processInfoTable);
        } finally {
            this.factory.unlockProcess(this.cache.getProcessInfoTable().getProcess(), LockType.WRITE);
        }

        return this.cache.getIndex();
    }

    public Map<Long, Long> getDate() {
        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcessInfoTable().getProcess(), LockType.READ);

            if (!this.isExist()) {
                throw new StatusNotExistedException();
            }

            ProcessInfoTableDefinition processInfoTable = this.init(process);

            ProcessInfoEntryDefinition processInfoEntry = processInfoTable.getByIndex(this.cache.getIndex());
            Map<Long, Long> processInfoEntryDate = processInfoEntry.getDate();

            return CollectionUtil.unmodifiable(processInfoEntryDate);
        } finally {
            this.factory.unlockProcess(this.cache.getProcessInfoTable().getProcess(), LockType.READ);
        }
    }

    public PathDefinition getPath() {
        DateTimeObject dateTime = this.coreManager.getDateTime();
        long nowDateTime = dateTime.getCurrentDateTime();

        PathDefinition path;

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcessInfoTable().getProcess(), LockType.WRITE);

            if (!this.isExist()) {
                throw new StatusNotExistedException();
            }

            ProcessInfoTableDefinition processInfoTable = this.init(process);

            ProcessInfoEntryDefinition processInfoEntry = processInfoTable.getByIndex(this.cache.getIndex());
            processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            path = processInfoEntry.getPath();

            this.flush(processInfoTable);
        } finally {
            this.factory.unlockProcess(this.cache.getProcessInfoTable().getProcess(), LockType.WRITE);
        }

        return path;
    }

    public InfoOpenDefinition getOpen() {
        DateTimeObject dateTime = this.coreManager.getDateTime();
        long nowDateTime = dateTime.getCurrentDateTime();

        InfoOpenDefinition infoOpen;

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcessInfoTable().getProcess(), LockType.WRITE);

            if (!this.isExist()) {
                throw new StatusNotExistedException();
            }

            ProcessInfoTableDefinition processInfoTable = this.init(process);

            ProcessInfoEntryDefinition processInfoEntry = processInfoTable.getByIndex(this.cache.getIndex());
            processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            infoOpen = processInfoEntry.getInfoOpen();

            this.flush(processInfoTable);
        } finally {
            this.factory.unlockProcess(this.cache.getProcessInfoTable().getProcess(), LockType.WRITE);
        }

        return infoOpen;
    }

    public boolean isUnsupportedDelete() {
        DateTimeObject dateTime = this.coreManager.getDateTime();
        long nowDateTime = dateTime.getCurrentDateTime();

        boolean unsupportedDelete;

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcessInfoTable().getProcess(), LockType.WRITE);

            if (!this.isExist()) {
                throw new StatusNotExistedException();
            }

            ProcessInfoTableDefinition processInfoTable = this.init(process);

            ProcessInfoEntryDefinition processInfoEntry = processInfoTable.getByIndex(this.cache.getIndex());
            processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            unsupportedDelete = processInfoEntry.isUnsupportedDelete();

            this.flush(processInfoTable);
        } finally {
            this.factory.unlockProcess(this.cache.getProcessInfoTable().getProcess(), LockType.WRITE);
        }

        return unsupportedDelete;
    }

    public void setUnsupportedDelete(boolean unsupportedDelete) {
        DateTimeObject dateTime = this.coreManager.getDateTime();
        long nowDateTime = dateTime.getCurrentDateTime();

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcessInfoTable().getProcess(), LockType.WRITE);

            if (!this.isExist()) {
                throw new StatusNotExistedException();
            }

            ProcessInfoTableDefinition processInfoTable = this.init(process);

            ProcessInfoEntryDefinition processInfoEntry = processInfoTable.getByIndex(this.cache.getIndex());
            processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            processInfoEntry.setUnsupportedDelete(unsupportedDelete);

            this.flush(processInfoTable);
        } finally {
            this.factory.unlockProcess(this.cache.getProcessInfoTable().getProcess(), LockType.WRITE);
        }
    }

    public synchronized InfoObject getInfo() {
        if (!this.isProcessCurrent.acquire()) {
            throw new StatusRelationshipErrorException();
        }

        DateTimeObject dateTime = this.coreManager.getDateTime();
        long nowDateTime = dateTime.getCurrentDateTime();

        PathDefinition path;

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcessInfoTable().getProcess(), LockType.WRITE);

            if (!this.isExist()) {
                throw new StatusNotExistedException();
            }

            ProcessInfoTableDefinition processInfoTable = this.init(process);

            ProcessInfoEntryDefinition processInfoEntry = processInfoTable.getByIndex(this.cache.getIndex());
            processInfoEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            path = processInfoEntry.getPath();

            this.flush(processInfoTable);
        } finally {
            this.factory.unlockProcess(this.cache.getProcessInfoTable().getProcess(), LockType.WRITE);
        }

        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);

        InfoObject info = objectManager.get(path);

        return info;
    }

    public void delete() {
        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcessInfoTable().getProcess(), LockType.WRITE);

            if (!this.isExist()) {
                throw new StatusNotExistedException();
            }

            ProcessInfoTableDefinition processInfoTable = this.init(process);

            ProcessInfoEntryDefinition processInfoEntry = processInfoTable.getByIndex(this.cache.getIndex());

            if (processInfoEntry.isUnsupportedDelete()) {
                throw new StatusDisabilityException();
            }

            processInfoEntry.getInfoOpen().setAttribute(InfoOpenAttributeType.CLOSE);

            processInfoTable.delete(processInfoEntry.getIndex());

            this.flush(processInfoTable);
        } finally {
            this.factory.unlockProcess(this.cache.getProcessInfoTable().getProcess(), LockType.WRITE);
        }

        this.cache.setIndex(null);
        this.factory.updateProcessInfoEntry(this.cache);
    }
}
