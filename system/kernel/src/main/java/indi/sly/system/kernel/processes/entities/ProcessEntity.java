package indi.sly.system.kernel.processes.entities;

import indi.sly.system.common.support.IDeepCloneable;
import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.ArrayUtils;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.UUIDUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "KernelProcesses")
public class ProcessEntity implements ISerializable<ProcessEntity> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnDefinition = "uniqueidentifier", name = "ID", nullable = false, updatable = false)
    protected UUID id;
    @Column(name = "Status", nullable = false)
    protected long status;
    @Column(columnDefinition = "uniqueidentifier", name = "ParentProcessID", nullable = true)
    protected UUID parentProcessID;
    @Column(columnDefinition = "uniqueidentifier", name = "SessionID", nullable = true)
    protected UUID sessionID;
    @Column(length = 4096, name = "Communication", nullable = false)
    protected byte[] communication;
    @Column(length = 4096, name = "Context", nullable = false)
    protected byte[] context;
    @Column(length = 4096, name = "HandleTable", nullable = false)
    protected byte[] handleTable;
    @Column(length = 4096, name = "Statistics", nullable = false)
    protected byte[] statistics;
    @Column(length = 4096, name = "Token", nullable = false)
    protected byte[] token;

    public UUID getID() {
        return this.id;
    }

    public void setID(UUID id) {
        this.id = id;
    }

    public long getStatus() {
        return this.status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public UUID getParentProcessID() {
        return this.parentProcessID;
    }

    public void setParentProcessID(UUID parentProcessID) {
        this.parentProcessID = parentProcessID;
    }

    public UUID getSessionID() {
        return this.sessionID;
    }

    public void setSessionID(UUID sessionID) {
        this.sessionID = sessionID;
    }

    public byte[] getCommunication() {
        return this.communication;
    }

    public void setCommunication(byte[] communication) {
        this.communication = communication;
    }

    public byte[] getContext() {
        return this.context;
    }

    public void setContext(byte[] context) {
        this.context = context;
    }

    public byte[] getHandleTable() {
        return this.handleTable;
    }

    public void setHandleTable(byte[] handleTable) {
        this.handleTable = handleTable;
    }

    public byte[] getStatistics() {
        return this.statistics;
    }

    public void setStatistics(byte[] statistics) {
        this.statistics = statistics;
    }

    public byte[] getToken() {
        return this.token;
    }

    public void setToken(byte[] token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessEntity that = (ProcessEntity) o;
        return status == that.status &&
                id.equals(that.id) &&
                Objects.equals(parentProcessID, that.parentProcessID) &&
                Objects.equals(sessionID, that.sessionID) &&
                Arrays.equals(communication, that.communication) &&
                Arrays.equals(context, that.context) &&
                Arrays.equals(handleTable, that.handleTable) &&
                Arrays.equals(statistics, that.statistics) &&
                Arrays.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, status, parentProcessID, sessionID);
        result = 31 * result + Arrays.hashCode(communication);
        result = 31 * result + Arrays.hashCode(context);
        result = 31 * result + Arrays.hashCode(handleTable);
        result = 31 * result + Arrays.hashCode(statistics);
        result = 31 * result + Arrays.hashCode(token);
        return result;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public ProcessEntity deepClone() {
        ProcessEntity process = new ProcessEntity();

        process.id = this.id;
        process.status = this.status;
        process.parentProcessID = this.parentProcessID;
        process.sessionID = this.sessionID;
        process.communication = ArrayUtils.copyBytes(this.communication);
        process.context = ArrayUtils.copyBytes(this.context);
        process.handleTable = ArrayUtils.copyBytes(this.handleTable);
        process.statistics = ArrayUtils.copyBytes(this.statistics);
        process.token = ArrayUtils.copyBytes(this.token);

        return process;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.id = UUIDUtils.readExternal(in);
        this.status = NumberUtils.readExternalLong(in);
        this.parentProcessID = UUIDUtils.readExternal(in);
        this.sessionID = UUIDUtils.readExternal(in);
        this.communication = NumberUtils.readExternalBytes(in);
        this.context = NumberUtils.readExternalBytes(in);
        this.handleTable = NumberUtils.readExternalBytes(in);
        this.statistics = NumberUtils.readExternalBytes(in);
        this.token = NumberUtils.readExternalBytes(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtils.writeExternal(out, this.id);
        NumberUtils.writeExternalLong(out, this.status);
        UUIDUtils.writeExternal(out, this.parentProcessID);
        UUIDUtils.writeExternal(out, this.sessionID);
        NumberUtils.writeExternalBytes(out, this.communication);
        NumberUtils.writeExternalBytes(out, this.context);
        NumberUtils.writeExternalBytes(out, this.handleTable);
        NumberUtils.writeExternalBytes(out, this.statistics);
        NumberUtils.writeExternalBytes(out, this.token);
    }
}
