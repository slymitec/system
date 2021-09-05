package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.supports.ArrayUtil;
import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.kernel.core.values.AEntity;

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
@Table(name = "Kernel_Processes")
public class ProcessEntity extends AEntity<ProcessEntity> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnDefinition = "uniqueidentifier", name = "ID", nullable = false, updatable = false)
    protected UUID id;
    @Column(name = "Status", nullable = false)
    protected long status;
    @Column(columnDefinition = "uniqueidentifier", name = "Parent_ProcessID", nullable = true)
    protected UUID parentProcessID;
    @Column(columnDefinition = "uniqueidentifier", name = "SessionID", nullable = true)
    protected UUID sessionID;
    @Column(length = 4096, name = "Communication", nullable = false)
    protected byte[] communication;
    @Column(length = 4096, name = "Context", nullable = false)
    protected byte[] context;
    @Column(length = 4096, name = "Info_Table", nullable = false)
    protected byte[] infoTable;
    @Column(length = 4096, name = "Counter_Statistics", nullable = false)
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

    public byte[] getInfoTable() {
        return this.infoTable;
    }

    public void setInfoTable(byte[] infoTable) {
        this.infoTable = infoTable;
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
                Arrays.equals(infoTable, that.infoTable) &&
                Arrays.equals(statistics, that.statistics) &&
                Arrays.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, status, parentProcessID, sessionID);
        result = 31 * result + Arrays.hashCode(communication);
        result = 31 * result + Arrays.hashCode(context);
        result = 31 * result + Arrays.hashCode(infoTable);
        result = 31 * result + Arrays.hashCode(statistics);
        result = 31 * result + Arrays.hashCode(token);
        return result;
    }

    @Override
    public ProcessEntity deepClone() {
        ProcessEntity process = new ProcessEntity();

        process.id = this.id;
        process.status = this.status;
        process.parentProcessID = this.parentProcessID;
        process.sessionID = this.sessionID;
        process.communication = ArrayUtil.copyBytes(this.communication);
        process.context = ArrayUtil.copyBytes(this.context);
        process.infoTable = ArrayUtil.copyBytes(this.infoTable);
        process.statistics = ArrayUtil.copyBytes(this.statistics);
        process.token = ArrayUtil.copyBytes(this.token);

        return process;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        this.id = UUIDUtil.readExternal(in);
        this.status = NumberUtil.readExternalLong(in);
        this.parentProcessID = UUIDUtil.readExternal(in);
        this.sessionID = UUIDUtil.readExternal(in);
        this.communication = NumberUtil.readExternalBytes(in);
        this.context = NumberUtil.readExternalBytes(in);
        this.infoTable = NumberUtil.readExternalBytes(in);
        this.statistics = NumberUtil.readExternalBytes(in);
        this.token = NumberUtil.readExternalBytes(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        UUIDUtil.writeExternal(out, this.id);
        NumberUtil.writeExternalLong(out, this.status);
        UUIDUtil.writeExternal(out, this.parentProcessID);
        UUIDUtil.writeExternal(out, this.sessionID);
        NumberUtil.writeExternalBytes(out, this.communication);
        NumberUtil.writeExternalBytes(out, this.context);
        NumberUtil.writeExternalBytes(out, this.infoTable);
        NumberUtil.writeExternalBytes(out, this.statistics);
        NumberUtil.writeExternalBytes(out, this.token);
    }
}
