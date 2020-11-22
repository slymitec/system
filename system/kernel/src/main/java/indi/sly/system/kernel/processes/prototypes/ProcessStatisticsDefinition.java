package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class ProcessStatisticsDefinition implements ISerializable<ProcessStatisticsDefinition> {
    private long infoCreate;
    private long infoGet;
    private long infoQuery;
    private long infoDelete;
    private long infoDump;
    private long infoOpen;
    private long infoClose;
    private long infoRead;
    private long infoWrite;

    public long getInfoCreate() {
        return this.infoCreate;
    }

    public void offsetInfoCreate(long offset) {
        this.infoCreate = this.infoCreate + offset;
    }

    public void setInfoCreate(long infoCreate) {
        this.infoCreate = infoCreate;
    }

    public long getInfoGet() {
        return this.infoGet;
    }

    public void offsetInfoGet(long offset) {
        this.infoGet = this.infoGet + offset;
    }

    public void setInfoGet(long infoGet) {
        this.infoGet = infoGet;
    }

    public long getInfoQuery() {
        return this.infoQuery;
    }

    public void offsetInfoQuery(long offset) {
        this.infoQuery = this.infoQuery + offset;
    }

    public void setInfoQuery(long infoQuery) {
        this.infoQuery = infoQuery;
    }

    public long getInfoDelete() {
        return this.infoDelete;
    }

    public void offsetInfoDelete(long offset) {
        this.infoDelete = this.infoDelete + offset;
    }

    public void setInfoDelete(long infoDelete) {
        this.infoDelete = infoDelete;
    }

    public long getInfoDump() {
        return this.infoDump;
    }

    public void offsetInfoDump(long offset) {
        this.infoDump = this.infoDump + offset;
    }

    public void setInfoDump(long infoDump) {
        this.infoDump = infoDump;
    }

    public long getInfoOpen() {
        return this.infoOpen;
    }

    public void offsetInfoOpen(long offset) {
        this.infoOpen = this.infoOpen + offset;
    }

    public void setInfoOpen(long infoOpen) {
        this.infoOpen = infoOpen;
    }

    public long getInfoClose() {
        return this.infoClose;
    }

    public void offsetInfoClose(long offset) {
        this.infoClose = this.infoClose + offset;
    }

    public void setInfoClose(long infoClose) {
        this.infoClose = infoClose;
    }

    public long getInfoRead() {
        return this.infoRead;
    }

    public void offsetInfoRead(long offset) {
        this.infoRead = this.infoRead + offset;
    }

    public void setInfoRead(long infoRead) {
        this.infoRead = infoRead;
    }

    public long getInfoWrite() {
        return this.infoWrite;
    }

    public void offsetInfoWrite(long offset) {
        this.infoWrite = this.infoWrite + offset;
    }

    public void setInfoWrite(long infoWrite) {
        this.infoWrite = infoWrite;
    }

    private long sharedReadCount;
    private long sharedReadBytes;
    private long sharedWriteCount;
    private long sharedWriteBytes;
    private long pipeReadCount;
    private long pipeReadBytes;
    private long pipeWriteCount;
    private long pipeWriteBytes;
    private long portReadCount;
    private long portReadBytes;
    private long portWriteCount;
    private long portWriteBytes;
    private long signalReadCount;
    private long signalWriteCount;

    public long getSharedReadCount() {
        return this.sharedReadCount;
    }

    public void offsetSharedReadCount(long offset) {
        this.sharedReadCount = this.sharedReadCount + offset;
    }

    public void setSharedReadCount(long sharedReadCount) {
        this.sharedReadCount = sharedReadCount;
    }

    public long getSharedReadBytes() {
        return this.sharedReadBytes;
    }

    public void offsetSharedReadBytes(long offset) {
        this.sharedReadBytes = this.sharedReadBytes + offset;
    }

    public void setSharedReadBytes(long sharedReadBytes) {
        this.sharedReadBytes = sharedReadBytes;
    }

    public long getSharedWriteCount() {
        return this.sharedWriteCount;
    }

    public void offsetSharedWriteCount(long offset) {
        this.sharedWriteCount = this.sharedWriteCount + offset;
    }

    public void setSharedWriteCount(long sharedWriteCount) {
        this.sharedWriteCount = sharedWriteCount;
    }

    public long getSharedWriteBytes() {
        return this.sharedWriteBytes;
    }

    public void offsetSharedWriteBytes(long offset) {
        this.sharedWriteBytes = this.sharedWriteBytes + offset;
    }

    public void setSharedWriteBytes(long sharedWriteBytes) {
        this.sharedWriteBytes = sharedWriteBytes;
    }

    public long getPipeReadCount() {
        return this.pipeReadCount;
    }

    public void offsetPipeReadCount(long offset) {
        this.pipeReadCount = this.pipeReadCount + offset;
    }

    public void setPipeReadCount(long pipeReadCount) {
        this.pipeReadCount = pipeReadCount;
    }

    public long getPipeReadBytes() {
        return this.pipeReadBytes;
    }

    public void offsetPipeReadBytes(long offset) {
        this.pipeReadBytes = this.pipeReadBytes + offset;
    }

    public void setPipeReadBytes(long pipeReadBytes) {
        this.pipeReadBytes = pipeReadBytes;
    }

    public long getPipeWriteCount() {
        return this.pipeWriteCount;
    }

    public void offsetPipeWriteCount(long offset) {
        this.pipeWriteCount = this.pipeWriteCount + offset;
    }

    public void setPipeWriteCount(long pipeWriteCount) {
        this.pipeWriteCount = pipeWriteCount;
    }

    public long getPipeWriteBytes() {
        return this.pipeWriteBytes;
    }

    public void offsetPipeWriteBytes(long offset) {
        this.pipeWriteBytes = this.pipeWriteBytes + offset;
    }

    public void setPipeWriteBytes(long pipeWriteBytes) {
        this.pipeWriteBytes = pipeWriteBytes;
    }

    public long getPortReadCount() {
        return this.portReadCount;
    }

    public void offsetPortReadCount(long offset) {
        this.portReadCount = this.portReadCount + offset;
    }

    public void setPortReadCount(long portReadCount) {
        this.portReadCount = portReadCount;
    }

    public long getPortReadBytes() {
        return this.portReadBytes;
    }

    public void offsetPortReadBytes(long offset) {
        this.portReadBytes = this.portReadBytes + offset;
    }

    public void setPortReadBytes(long portReadBytes) {
        this.portReadBytes = portReadBytes;
    }

    public long getPortWriteCount() {
        return this.portWriteCount;
    }

    public void offsetPortWriteCount(long offset) {
        this.portWriteCount = this.portWriteCount + offset;
    }

    public void setPortWriteCount(long portWriteCount) {
        this.portWriteCount = portWriteCount;
    }

    public long getPortWriteBytes() {
        return this.portWriteBytes;
    }

    public void offsetPortWriteBytes(long offset) {
        this.portWriteBytes = this.portWriteBytes + offset;
    }

    public void setPortWriteBytes(long portWriteBytes) {
        this.portWriteBytes = portWriteBytes;
    }

    public long getSignalReadCount() {
        return this.signalReadCount;
    }

    public void offsetSignalReadCount(long offset) {
        this.signalReadCount = this.signalReadCount + offset;
    }

    public void setSignalReadCount(long signalReadCount) {
        this.signalReadCount = signalReadCount;
    }

    public long getSignalWriteCount() {
        return this.signalWriteCount;
    }

    public void offsetSignalWriteCount(long offset) {
        this.signalWriteCount = this.signalWriteCount + offset;
    }

    public void setSignalWriteCount(long signalWriteCount) {
        this.signalWriteCount = signalWriteCount;
    }

    private long ioCreate;
    private long ioStatus;
    private long ioReadCount;
    private long ioReadBytes;
    private long ioWriteCount;
    private long ioWriteBytes;

    public long getIoCreate() {
        return this.ioCreate;
    }

    public void offsetIoCreate(long offset) {
        this.ioCreate = this.ioCreate + offset;
    }

    public void setIoCreate(long ioCreate) {
        this.ioCreate = ioCreate;
    }

    public long getIoStatus() {
        return this.ioStatus;
    }

    public void offsetIoStatus(long offset) {
        this.ioStatus = this.ioStatus + offset;
    }

    public void setIoStatus(long ioStatus) {
        this.ioStatus = ioStatus;
    }

    public long getIoReadCount() {
        return this.ioReadCount;
    }

    public void offsetIoReadCount(long offset) {
        this.ioReadCount = this.ioReadCount + offset;
    }

    public void setIoReadCount(long ioReadCount) {
        this.ioReadCount = ioReadCount;
    }

    public long getIoReadBytes() {
        return this.ioReadBytes;
    }

    public void offsetIoReadBytes(long offset) {
        this.ioReadBytes = this.ioReadBytes + offset;
    }

    public void setIoReadBytes(long ioReadBytes) {
        this.ioReadBytes = ioReadBytes;
    }

    public long getIoWriteCount() {
        return this.ioWriteCount;
    }

    public void offsetIoWriteCount(long offset) {
        this.ioWriteCount = this.ioWriteCount + offset;
    }

    public void setIoWriteCount(long ioWriteCount) {
        this.ioWriteCount = ioWriteCount;
    }

    public long getIoWriteBytes() {
        return this.ioWriteBytes;
    }

    public void offsetIoWriteBytes(long offset) {
        this.ioWriteBytes = this.ioWriteBytes + offset;
    }

    public void setIoWriteBytes(long ioWriteBytes) {
        this.ioWriteBytes = ioWriteBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessStatisticsDefinition that = (ProcessStatisticsDefinition) o;
        return infoCreate == that.infoCreate &&
                infoGet == that.infoGet &&
                infoQuery == that.infoQuery &&
                infoDelete == that.infoDelete &&
                infoDump == that.infoDump &&
                infoOpen == that.infoOpen &&
                infoClose == that.infoClose &&
                infoRead == that.infoRead &&
                infoWrite == that.infoWrite &&
                sharedReadCount == that.sharedReadCount &&
                sharedReadBytes == that.sharedReadBytes &&
                sharedWriteCount == that.sharedWriteCount &&
                sharedWriteBytes == that.sharedWriteBytes &&
                pipeReadCount == that.pipeReadCount &&
                pipeReadBytes == that.pipeReadBytes &&
                pipeWriteCount == that.pipeWriteCount &&
                pipeWriteBytes == that.pipeWriteBytes &&
                portReadCount == that.portReadCount &&
                portReadBytes == that.portReadBytes &&
                portWriteCount == that.portWriteCount &&
                portWriteBytes == that.portWriteBytes &&
                signalReadCount == that.signalReadCount &&
                signalWriteCount == that.signalWriteCount &&
                ioCreate == that.ioCreate &&
                ioStatus == that.ioStatus &&
                ioReadCount == that.ioReadCount &&
                ioReadBytes == that.ioReadBytes &&
                ioWriteCount == that.ioWriteCount &&
                ioWriteBytes == that.ioWriteBytes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(infoCreate, infoGet, infoQuery, infoDelete, infoDump, infoOpen, infoClose, infoRead,
                infoWrite, sharedReadCount, sharedReadBytes, sharedWriteCount, sharedWriteBytes, pipeReadCount,
                pipeReadBytes, pipeWriteCount, pipeWriteBytes, portReadCount, portReadBytes, portWriteCount,
                portWriteBytes, signalReadCount, signalWriteCount, ioCreate, ioStatus, ioReadCount, ioReadBytes,
                ioWriteCount, ioWriteBytes);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public ProcessStatisticsDefinition deepClone() {
        ProcessStatisticsDefinition processStatistics = new ProcessStatisticsDefinition();

        processStatistics.infoCreate = this.infoCreate;
        processStatistics.infoGet = this.infoGet;
        processStatistics.infoQuery = this.infoQuery;
        processStatistics.infoDelete = this.infoDelete;
        processStatistics.infoDump = this.infoDump;
        processStatistics.infoOpen = this.infoOpen;
        processStatistics.infoClose = this.infoClose;
        processStatistics.infoRead = this.infoRead;
        processStatistics.infoWrite = this.infoWrite;

        processStatistics.sharedReadCount = this.sharedReadCount;
        processStatistics.sharedReadBytes = this.sharedReadBytes;
        processStatistics.sharedWriteCount = this.sharedWriteCount;
        processStatistics.sharedWriteBytes = this.sharedWriteBytes;
        processStatistics.pipeReadCount = this.pipeReadCount;
        processStatistics.pipeReadBytes = this.pipeReadBytes;
        processStatistics.pipeWriteCount = this.pipeWriteCount;
        processStatistics.pipeWriteBytes = this.pipeWriteBytes;
        processStatistics.portReadCount = this.portReadCount;
        processStatistics.portReadBytes = this.portReadBytes;
        processStatistics.portWriteCount = this.portWriteCount;
        processStatistics.portWriteBytes = this.portWriteBytes;
        processStatistics.signalReadCount = this.signalReadCount;
        processStatistics.signalWriteCount = this.signalWriteCount;

        processStatistics.ioCreate = this.ioCreate;
        processStatistics.ioStatus = this.ioStatus;
        processStatistics.ioReadCount = this.ioReadCount;
        processStatistics.ioReadBytes = this.ioReadBytes;
        processStatistics.ioWriteCount = this.ioWriteCount;
        processStatistics.ioWriteBytes = this.ioWriteBytes;

        return processStatistics;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        this.infoCreate = NumberUtils.readExternalLong(in);
        this.infoGet = NumberUtils.readExternalLong(in);
        this.infoQuery = NumberUtils.readExternalLong(in);
        this.infoDelete = NumberUtils.readExternalLong(in);
        this.infoDump = NumberUtils.readExternalLong(in);
        this.infoOpen = NumberUtils.readExternalLong(in);
        this.infoClose = NumberUtils.readExternalLong(in);
        this.infoRead = NumberUtils.readExternalLong(in);
        this.infoWrite = NumberUtils.readExternalLong(in);

        this.sharedReadCount = NumberUtils.readExternalLong(in);
        this.sharedReadBytes = NumberUtils.readExternalLong(in);
        this.sharedWriteCount = NumberUtils.readExternalLong(in);
        this.sharedWriteBytes = NumberUtils.readExternalLong(in);
        this.pipeReadCount = NumberUtils.readExternalLong(in);
        this.pipeReadBytes = NumberUtils.readExternalLong(in);
        this.pipeWriteCount = NumberUtils.readExternalLong(in);
        this.pipeWriteBytes = NumberUtils.readExternalLong(in);
        this.portReadCount = NumberUtils.readExternalLong(in);
        this.portReadBytes = NumberUtils.readExternalLong(in);
        this.portWriteCount = NumberUtils.readExternalLong(in);
        this.portWriteBytes = NumberUtils.readExternalLong(in);
        this.signalReadCount = NumberUtils.readExternalLong(in);
        this.signalWriteCount = NumberUtils.readExternalLong(in);

        this.ioCreate = NumberUtils.readExternalLong(in);
        this.ioStatus = NumberUtils.readExternalLong(in);
        this.ioReadCount = NumberUtils.readExternalLong(in);
        this.ioReadBytes = NumberUtils.readExternalLong(in);
        this.ioWriteCount = NumberUtils.readExternalLong(in);
        this.ioWriteBytes = NumberUtils.readExternalLong(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtils.writeExternalLong(out, this.infoCreate);
        NumberUtils.writeExternalLong(out, this.infoGet);
        NumberUtils.writeExternalLong(out, this.infoQuery);
        NumberUtils.writeExternalLong(out, this.infoDelete);
        NumberUtils.writeExternalLong(out, this.infoDump);
        NumberUtils.writeExternalLong(out, this.infoOpen);
        NumberUtils.writeExternalLong(out, this.infoClose);
        NumberUtils.writeExternalLong(out, this.infoRead);
        NumberUtils.writeExternalLong(out, this.infoWrite);

        NumberUtils.writeExternalLong(out, this.sharedReadCount);
        NumberUtils.writeExternalLong(out, this.sharedReadBytes);
        NumberUtils.writeExternalLong(out, this.sharedWriteCount);
        NumberUtils.writeExternalLong(out, this.sharedWriteBytes);
        NumberUtils.writeExternalLong(out, this.pipeReadCount);
        NumberUtils.writeExternalLong(out, this.pipeReadBytes);
        NumberUtils.writeExternalLong(out, this.pipeWriteCount);
        NumberUtils.writeExternalLong(out, this.pipeWriteBytes);
        NumberUtils.writeExternalLong(out, this.portReadCount);
        NumberUtils.writeExternalLong(out, this.portReadBytes);
        NumberUtils.writeExternalLong(out, this.portWriteCount);
        NumberUtils.writeExternalLong(out, this.portWriteBytes);
        NumberUtils.writeExternalLong(out, this.signalReadCount);
        NumberUtils.writeExternalLong(out, this.signalWriteCount);

        NumberUtils.writeExternalLong(out, this.ioCreate);
        NumberUtils.writeExternalLong(out, this.ioStatus);
        NumberUtils.writeExternalLong(out, this.ioReadCount);
        NumberUtils.writeExternalLong(out, this.ioReadBytes);
        NumberUtils.writeExternalLong(out, this.ioWriteCount);
        NumberUtils.writeExternalLong(out, this.ioWriteBytes);
    }
}
