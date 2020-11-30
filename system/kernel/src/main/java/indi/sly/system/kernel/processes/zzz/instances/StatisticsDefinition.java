package indi.sly.system.kernel.processes.zzz.instances;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;

public class StatisticsDefinition implements ISerializable<StatisticsDefinition> {
    private long infoCreate;
    private long infoGet;
    private long infoQuery;
    private long infoDelete;
    private long infoDump;
    private long infoOpen;
    private long infoClose;
    private long infoRead;
    private long infoWrite;
    private long infoFree;
    private long infoException;

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

    public long getInfoFree() {
        return infoFree;
    }

    public void setInfoFree(long infoFree) {
        this.infoFree = infoFree;
    }

    public long getInfoException() {
        return infoException;
    }

    public void setInfoException(long infoException) {
        this.infoException = infoException;
    }

    private long portReceive;
    private long portSend;

    public long getPortReceive() {
        return portReceive;
    }

    public void setPortReceive(long portReceive) {
        this.portReceive = portReceive;
    }

    private long ioCreate;
    private long ioStatus;
    private long ioReadCount;
    private long ioReadBytes;
    private long ioWriteCount;
    private long ioWriteBytes;
    private long ioException;

    public long getPortSend() {
        return portSend;
    }

    public void setPortSend(long portSend) {
        this.portSend = portSend;
    }

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

    public long getIoException() {
        return ioException;
    }

    public void setIoException(long ioException) {
        this.ioException = ioException;
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
        this.infoFree = NumberUtils.readExternalLong(in);
        this.infoException = NumberUtils.readExternalLong(in);

        this.portReceive = NumberUtils.readExternalLong(in);
        this.portSend = NumberUtils.readExternalLong(in);

        this.ioCreate = NumberUtils.readExternalLong(in);
        this.ioStatus = NumberUtils.readExternalLong(in);
        this.ioReadCount = NumberUtils.readExternalLong(in);
        this.ioReadBytes = NumberUtils.readExternalLong(in);
        this.ioWriteCount = NumberUtils.readExternalLong(in);
        this.ioWriteBytes = NumberUtils.readExternalLong(in);
        this.ioException = NumberUtils.readExternalLong(in);
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
        NumberUtils.writeExternalLong(out, this.infoFree);
        NumberUtils.writeExternalLong(out, this.infoException);

        NumberUtils.writeExternalLong(out, this.portReceive);
        NumberUtils.writeExternalLong(out, this.portSend);

        NumberUtils.writeExternalLong(out, this.ioCreate);
        NumberUtils.writeExternalLong(out, this.ioStatus);
        NumberUtils.writeExternalLong(out, this.ioReadCount);
        NumberUtils.writeExternalLong(out, this.ioReadBytes);
        NumberUtils.writeExternalLong(out, this.ioWriteCount);
        NumberUtils.writeExternalLong(out, this.ioWriteBytes);
        NumberUtils.writeExternalLong(out, this.ioException);
    }

    @Override
    public StatisticsDefinition deepClone() {
        return null;
    }
}
