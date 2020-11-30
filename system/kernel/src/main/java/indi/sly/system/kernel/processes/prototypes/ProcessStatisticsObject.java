package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.StatusRelationshipErrorException;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.prototypes.ABytesProcessObject;
import indi.sly.system.kernel.processes.definitions.ProcessStatisticsDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessStatisticsObject extends ABytesProcessObject {
    @Override
    protected void read(byte[] source) {
        this.processStatistics = ObjectUtils.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtils.transferToByteArray(this.processStatistics);
    }

    private ProcessObject process;
    private ProcessStatisticsDefinition processStatistics;

    public void setProcess(ProcessObject process) {
        this.process = process;
    }

    public long getDate(long dataTimeType) {
        this.init();

        Long value = this.processStatistics.getDate().getOrDefault(dataTimeType, null);

        if (ObjectUtils.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        return value.longValue();
    }

    public void setDate(long dataTimeType, long value) {
        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.getDate().put(dataTimeType, value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getStatusCumulation() {
        this.init();

        return this.processStatistics.getStatusCumulation();
    }

    public void addStatusCumulation(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetStatusCumulation(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getThreadCumulation() {
        this.init();

        return this.processStatistics.getThreadCumulation();
    }

    public void addThreadCumulation(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetThreadCumulation(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getPipeCumulation() {
        this.init();

        return this.processStatistics.getPipeCumulation();
    }

    public void addPipeCumulation(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetPipeCumulation(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getPortCumulation() {
        this.init();

        return this.processStatistics.getPortCumulation();
    }

    public void addPortCumulation(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetPortCumulation(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getSignalCumulation() {
        this.init();

        return this.processStatistics.getSignalCumulation();
    }

    public void addSignalCumulation(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetSignalCumulation(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getHandleCumulation() {
        this.init();

        return this.processStatistics.getHandleCumulation();
    }

    public void addHandleCumulation(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetHandleCumulation(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getInfoCreate() {
        this.init();

        return this.processStatistics.getInfoCreate();
    }

    public void addInfoCreate(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetInfoCreate(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getInfoGet() {
        this.init();

        return this.processStatistics.getInfoGet();
    }

    public void addInfoGet(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetInfoGet(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getInfoQuery() {
        this.init();

        return this.processStatistics.getInfoQuery();
    }

    public void addInfoQuery(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetInfoQuery(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getInfoDelete() {
        this.init();

        return this.processStatistics.getInfoDelete();
    }

    public void addInfoDelete(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetInfoDelete(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getInfoDump() {
        this.init();

        return this.processStatistics.getInfoDump();
    }

    public void addInfoDump(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetInfoDump(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getInfoOpen() {
        this.init();

        return this.processStatistics.getInfoOpen();
    }

    public void addInfoOpen(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetInfoOpen(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getInfoClose() {
        this.init();

        return this.processStatistics.getInfoClose();
    }

    public void addInfoClose(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetInfoClose(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getInfoRead() {
        this.init();

        return this.processStatistics.getInfoRead();
    }

    public void addInfoRead(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetInfoRead(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getInfoWrite() {
        this.init();

        return this.processStatistics.getInfoWrite();
    }

    public void addInfoWrite(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetInfoWrite(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getSharedReadCount() {
        this.init();

        return this.processStatistics.getSharedReadCount();
    }

    public void addSharedReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetSharedReadCount(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getSharedReadBytes() {
        this.init();

        return this.processStatistics.getSharedReadBytes();
    }

    public void addSharedReadBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetSharedReadBytes(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getSharedWriteCount() {
        this.init();

        return this.processStatistics.getSharedWriteCount();
    }

    public void addSharedWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetSharedWriteCount(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getSharedWriteBytes() {
        this.init();

        return this.processStatistics.getSharedWriteBytes();
    }

    public void addSharedWriteBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetSharedWriteBytes(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getPipeReadCount() {
        this.init();

        return this.processStatistics.getPipeReadCount();
    }

    public void addPipeReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetPipeReadCount(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getPipeReadBytes() {
        this.init();

        return this.processStatistics.getPipeReadBytes();
    }

    public void addPipeReadBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetPipeReadBytes(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getPipeWriteCount() {
        this.init();

        return this.processStatistics.getPipeWriteCount();
    }

    public void addPipeWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetPipeWriteCount(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getPipeWriteBytes() {
        this.init();

        return this.processStatistics.getPipeWriteBytes();
    }

    public void addPipeWriteBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetPipeWriteBytes(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getPortReadCount() {
        this.init();

        return this.processStatistics.getPortReadCount();
    }

    public void addPortReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetPortReadCount(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getPortReadBytes() {
        this.init();

        return this.processStatistics.getPortReadBytes();
    }

    public void addPortReadBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetPortReadBytes(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getPortWriteCount() {
        this.init();

        return this.processStatistics.getPortWriteCount();
    }

    public void addPortWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetPortWriteCount(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getPortWriteBytes() {
        this.init();

        return this.processStatistics.getPortWriteBytes();
    }

    public void addPortWriteBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetPortWriteBytes(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getSignalReadCount() {
        this.init();

        return this.processStatistics.getSignalReadCount();
    }

    public void addSignalReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetSignalReadCount(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getSignalWriteCount() {
        this.init();

        return this.processStatistics.getSignalWriteCount();
    }

    public void addSignalWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetSignalWriteCount(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getIoCreate() {
        this.init();

        return this.processStatistics.getIoCreate();
    }

    public void addIoCreate(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetIoCreate(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getIoStatus() {
        this.init();

        return this.processStatistics.getIoStatus();
    }

    public void addIoStatus(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetIoStatus(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getIoReadCount() {
        this.init();

        return this.processStatistics.getIoReadCount();
    }

    public void addIoReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetIoReadCount(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getIoReadBytes() {
        this.init();

        return this.processStatistics.getIoReadBytes();
    }

    public void addIoReadBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetIoReadBytes(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getIoWriteCount() {
        this.init();

        return this.processStatistics.getIoWriteCount();
    }

    public void addIoWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetIoWriteCount(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getIoWriteBytes() {
        this.init();

        return this.processStatistics.getIoWriteBytes();
    }

    public void addIoWriteBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.offsetIoWriteBytes(value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }
}
