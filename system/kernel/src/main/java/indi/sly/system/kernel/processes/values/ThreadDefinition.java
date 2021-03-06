package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

public class ThreadDefinition extends ADefinition<ThreadDefinition> {
    public ThreadDefinition() {
        this.id = UUIDUtil.createRandom();
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

    public ThreadStatisticsDefinition getStatistics() {
        return this.statistics;
    }

    public void setStatistics(ThreadStatisticsDefinition statistics) {
        this.statistics = statistics;
    }

    public ThreadContextDefinition getContext() {
        return this.context;
    }

    public void setContext(ThreadContextDefinition context) {
        this.context = context;
    }

    @Override
    public ThreadDefinition deepClone() {
        ThreadDefinition thread = new ThreadDefinition();

        thread.id = this.id;
        thread.processID = this.processID;
        thread.statistics = this.statistics.deepClone();
        thread.context = this.context.deepClone();

        return null;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.id = UUIDUtil.readExternal(in);
        this.processID = UUIDUtil.readExternal(in);
        this.statistics = ObjectUtil.readExternal(in);
        this.context = ObjectUtil.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtil.writeExternal(out, this.id);
        UUIDUtil.writeExternal(out, this.processID);
        ObjectUtil.writeExternal(out, this.statistics);
        ObjectUtil.writeExternal(out, this.context);
    }
}
