package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessPrototype;
import indi.sly.system.kernel.processes.values.ProcessStatisticsDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessStatisticsObject extends ABytesValueProcessPrototype<ProcessStatisticsDefinition> {
    protected ProcessObject process;

    public long getDate(long dataTime) {
        this.init();

        Long value = this.value.getDate().getOrDefault(dataTime, null);

        if (ObjectUtil.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        return value;
    }

    public void setDate(long dataTimeType, long value) {
        this.lock(LockType.WRITE);
        this.init();

        this.value.getDate().put(dataTimeType, value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getStatusCumulation() {
        this.init();

        return this.value.getStatusCumulation();
    }

    public void addStatusCumulation(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetStatusCumulation(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getThreadCumulation() {
        this.init();

        return this.value.getThreadCumulation();
    }

    public void addThreadCumulation(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetThreadCumulation(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getInfoCreate() {
        this.init();

        return this.value.getInfoCreate();
    }

    public void addInfoCreate(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetInfoCreate(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getInfoGet() {
        this.init();

        return this.value.getInfoGet();
    }

    public void addInfoGet(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetInfoGet(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getInfoQuery() {
        this.init();

        return this.value.getInfoQuery();
    }

    public void addInfoQuery(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetInfoQuery(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getInfoDelete() {
        this.init();

        return this.value.getInfoDelete();
    }

    public void addInfoDelete(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetInfoDelete(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getInfoDump() {
        this.init();

        return this.value.getInfoDump();
    }

    public void addInfoDump(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetInfoDump(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getInfoOpen() {
        this.init();

        return this.value.getInfoOpen();
    }

    public void addInfoOpen(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetInfoOpen(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getInfoClose() {
        this.init();

        return this.value.getInfoClose();
    }

    public void addInfoClose(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetInfoClose(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getInfoRead() {
        this.init();

        return this.value.getInfoRead();
    }

    public void addInfoRead(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetInfoRead(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getInfoWrite() {
        this.init();

        return this.value.getInfoWrite();
    }

    public void addInfoWrite(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetInfoWrite(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getSharedReadCount() {
        this.init();

        return this.value.getSharedReadCount();
    }

    public void addSharedReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetSharedReadCount(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getSharedReadBytes() {
        this.init();

        return this.value.getSharedReadBytes();
    }

    public void addSharedReadBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetSharedReadBytes(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getSharedWriteCount() {
        this.init();

        return this.value.getSharedWriteCount();
    }

    public void addSharedWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetSharedWriteCount(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getSharedWriteBytes() {
        this.init();

        return this.value.getSharedWriteBytes();
    }

    public void addSharedWriteBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetSharedWriteBytes(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getPortCount() {
        this.init();

        return this.value.getPortCount();
    }

    public void addPortCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetPortCount(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getPortReadCount() {
        this.init();

        return this.value.getPortReadCount();
    }

    public void addPortReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetPortReadCount(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getPortReadBytes() {
        this.init();

        return this.value.getPortReadBytes();
    }

    public void addPortReadBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetPortReadBytes(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getPortWriteCount() {
        this.init();

        return this.value.getPortWriteCount();
    }

    public void addPortWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetPortWriteCount(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getPortWriteBytes() {
        this.init();

        return this.value.getPortWriteBytes();
    }

    public void addPortWriteBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetPortWriteBytes(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getSignalReadCount() {
        this.init();

        return this.value.getSignalReadCount();
    }

    public void addSignalReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetSignalReadCount(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getSignalWriteCount() {
        this.init();

        return this.value.getSignalWriteCount();
    }

    public void addSignalWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetSignalWriteCount(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getIoCreate() {
        this.init();

        return this.value.getIoCreate();
    }

    public void addIoCreate(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetIoCreate(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getIoStatus() {
        this.init();

        return this.value.getIoStatus();
    }

    public void addIoStatus(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetIoStatus(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getIoReadCount() {
        this.init();

        return this.value.getIoReadCount();
    }

    public void addIoReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetIoReadCount(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getIoReadBytes() {
        this.init();

        return this.value.getIoReadBytes();
    }

    public void addIoReadBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetIoReadBytes(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getIoWriteCount() {
        this.init();

        return this.value.getIoWriteCount();
    }

    public void addIoWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetIoWriteCount(value);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getIoWriteBytes() {
        this.init();

        return this.value.getIoWriteBytes();
    }

    public void addIoWriteBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetIoWriteBytes(value);

        this.fresh();
        this.lock(LockType.NONE);
    }
}
