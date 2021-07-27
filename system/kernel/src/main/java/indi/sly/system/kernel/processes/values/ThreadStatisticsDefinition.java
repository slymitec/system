package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;

public class ThreadStatisticsDefinition extends ADefinition<ThreadStatisticsDefinition> {
    public ThreadStatisticsDefinition() {
        this.date = new HashMap<>();
    }

    private final Map<Long, Long> date;

    public Map<Long, Long> getDate() {
        return this.date;
    }

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
    private long portCount;
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

    public long getPortCount() {
        return this.portCount;
    }

    public void offsetPortCount(long offset) {
        this.portCount = this.portCount + offset;
    }

    public void setPortCount(long portCount) {
        this.portCount = portCount;
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
}
