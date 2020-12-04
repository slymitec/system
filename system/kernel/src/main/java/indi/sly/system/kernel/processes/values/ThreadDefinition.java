package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

public class ThreadDefinition implements ISerializable<ThreadDefinition> {
    public ThreadDefinition() {
        this.statistics = new ThreadStatisticsDefinition();
        this.context = new ThreadContextDefinition();
    }

    private UUID id;
    private UUID processID;
    private ThreadStatisticsDefinition statistics;
    private ThreadContextDefinition context;

    public UUID getID() {
        return this.id;
    }

    public void setID(UUID id) {
        this.id = id;
    }

    public UUID getProcessID() {
        return this.processID;
    }

    public void setProcessID(UUID processID) {
        this.processID = processID;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public ThreadDefinition deepClone() {
        ThreadDefinition thread = new ThreadDefinition();

        thread.id = this.id;
        thread.processID = this.processID;
        thread.statistics = this.statistics;
        thread.context = this.context;

        return null;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.id = UUIDUtils.readExternal(in);
        this.processID = UUIDUtils.readExternal(in);
        this.statistics = ObjectUtils.readExternal(in);
        this.context = ObjectUtils.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtils.writeExternal(out, this.id);
        UUIDUtils.writeExternal(out, this.processID);
        ObjectUtils.writeExternal(out, this.statistics);
        ObjectUtils.writeExternal(out, this.context);
    }
}
