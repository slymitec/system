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
        return infoCreate;
    }

    public void setInfoCreate(long infoCreate) {
        this.infoCreate = infoCreate;
    }

    public long getInfoGet() {
        return infoGet;
    }

    public void setInfoGet(long infoGet) {
        this.infoGet = infoGet;
    }

    public long getInfoQuery() {
        return infoQuery;
    }

    public void setInfoQuery(long infoQuery) {
        this.infoQuery = infoQuery;
    }

    public long getInfoDelete() {
        return infoDelete;
    }

    public void setInfoDelete(long infoDelete) {
        this.infoDelete = infoDelete;
    }

    public long getInfoDump() {
        return infoDump;
    }

    public void setInfoDump(long infoDump) {
        this.infoDump = infoDump;
    }

    public long getInfoOpen() {
        return infoOpen;
    }

    public void setInfoOpen(long infoOpen) {
        this.infoOpen = infoOpen;
    }

    public long getInfoClose() {
        return infoClose;
    }

    public void setInfoClose(long infoClose) {
        this.infoClose = infoClose;
    }

    public long getInfoRead() {
        return infoRead;
    }

    public void setInfoRead(long infoRead) {
        this.infoRead = infoRead;
    }

    public long getInfoWrite() {
        return infoWrite;
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

    public void setSharedReadCount(long sharedReadCount) {
        this.sharedReadCount = sharedReadCount;
    }

    public long getSharedReadBytes() {
        return this.sharedReadBytes;
    }

    public void setSharedReadBytes(long sharedReadBytes) {
        this.sharedReadBytes = sharedReadBytes;
    }

    public long getSharedWriteCount() {
        return this.sharedWriteCount;
    }

    public void setSharedWriteCount(long sharedWriteCount) {
        this.sharedWriteCount = sharedWriteCount;
    }

    public long getSharedWriteBytes() {
        return this.sharedWriteBytes;
    }

    public void setSharedWriteBytes(long sharedWriteBytes) {
        this.sharedWriteBytes = sharedWriteBytes;
    }

    public long getPipeReadCount() {
        return this.pipeReadCount;
    }

    public void setPipeReadCount(long pipeReadCount) {
        this.pipeReadCount = pipeReadCount;
    }

    public long getPipeReadBytes() {
        return this.pipeReadBytes;
    }

    public void setPipeReadBytes(long pipeReadBytes) {
        this.pipeReadBytes = pipeReadBytes;
    }

    public long getPipeWriteCount() {
        return this.pipeWriteCount;
    }

    public void setPipeWriteCount(long pipeWriteCount) {
        this.pipeWriteCount = pipeWriteCount;
    }

    public long getPipeWriteBytes() {
        return this.pipeWriteBytes;
    }

    public void setPipeWriteBytes(long pipeWriteBytes) {
        this.pipeWriteBytes = pipeWriteBytes;
    }

    public long getPortReadCount() {
        return this.portReadCount;
    }

    public void setPortReadCount(long portReadCount) {
        this.portReadCount = portReadCount;
    }

    public long getPortReadBytes() {
        return this.portReadBytes;
    }

    public void setPortReadBytes(long portReadBytes) {
        this.portReadBytes = portReadBytes;
    }

    public long getPortWriteCount() {
        return this.portWriteCount;
    }

    public void setPortWriteCount(long portWriteCount) {
        this.portWriteCount = portWriteCount;
    }

    public long getPortWriteBytes() {
        return this.portWriteBytes;
    }

    public void setPortWriteBytes(long portWriteBytes) {
        this.portWriteBytes = portWriteBytes;
    }

    public long getSignalReadCount() {
        return this.signalReadCount;
    }

    public void setSignalReadCount(long signalReadCount) {
        this.signalReadCount = signalReadCount;
    }

    public long getSignalWriteCount() {
        return this.signalWriteCount;
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
        return ioCreate;
    }

    public void setIoCreate(long ioCreate) {
        this.ioCreate = ioCreate;
    }

    public long getIoStatus() {
        return ioStatus;
    }

    public void setIoStatus(long ioStatus) {
        this.ioStatus = ioStatus;
    }

    public long getIoReadCount() {
        return ioReadCount;
    }

    public void setIoReadCount(long ioReadCount) {
        this.ioReadCount = ioReadCount;
    }

    public long getIoReadBytes() {
        return ioReadBytes;
    }

    public void setIoReadBytes(long ioReadBytes) {
        this.ioReadBytes = ioReadBytes;
    }

    public long getIoWriteCount() {
        return ioWriteCount;
    }

    public void setIoWriteCount(long ioWriteCount) {
        this.ioWriteCount = ioWriteCount;
    }

    public long getIoWriteBytes() {
        return ioWriteBytes;
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
