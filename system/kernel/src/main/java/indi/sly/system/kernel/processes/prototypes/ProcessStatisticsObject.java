package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.prototypes.ABytesProcessObject;
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

    private ProcessStatisticsDefinition processStatistics;

    public long getInfoCreate() {
        this.init();

        return this.processStatistics.getInfoCreate();
    }

    public void addInfoCreate(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setInfoCreate(this.processStatistics.getInfoCreate() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setInfoGet(this.processStatistics.getInfoGet() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setInfoQuery(this.processStatistics.getInfoQuery() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setInfoDelete(this.processStatistics.getInfoDelete() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setInfoDump(this.processStatistics.getInfoDump() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setInfoOpen(this.processStatistics.getInfoOpen() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setInfoClose(this.processStatistics.getInfoClose() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setInfoRead(this.processStatistics.getInfoRead() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setInfoWrite(this.processStatistics.getInfoWrite() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setSharedReadCount(this.processStatistics.getSharedReadCount() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setSharedReadBytes(this.processStatistics.getSharedReadBytes() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setSharedWriteCount(this.processStatistics.getSharedWriteCount() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setSharedWriteBytes(this.processStatistics.getSharedWriteBytes() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setPipeReadCount(this.processStatistics.getPipeReadCount() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setPipeReadBytes(this.processStatistics.getPipeReadBytes() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setPipeWriteCount(this.processStatistics.getPipeWriteCount() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setPipeWriteBytes(this.processStatistics.getPipeWriteBytes() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setPortReadCount(this.processStatistics.getPortReadCount() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setPortReadBytes(this.processStatistics.getPortReadBytes() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setPortWriteCount(this.processStatistics.getPortWriteCount() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setPortWriteBytes(this.processStatistics.getPortWriteBytes() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setSignalReadCount(this.processStatistics.getSignalReadCount() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setSignalWriteCount(this.processStatistics.getSignalWriteCount() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setIoCreate(this.processStatistics.getIoCreate() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setIoStatus(this.processStatistics.getIoStatus() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setIoReadCount(this.processStatistics.getIoReadCount() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setIoReadBytes(this.processStatistics.getIoReadBytes() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setIoWriteCount(this.processStatistics.getIoWriteCount() + value);

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

        this.lock(LockTypes.WRITE);
        this.init();

        this.processStatistics.setIoWriteBytes(this.processStatistics.getIoWriteBytes() + value);

        this.fresh();
        this.lock(LockTypes.NONE);
    }
}
