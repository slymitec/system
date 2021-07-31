package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.util.UUID;

public class ThreadDefinition extends ADefinition<ThreadDefinition> {
    public ThreadDefinition() {
        this.id = UUIDUtil.createRandom();
        this.status = ThreadStatusType.NULL;
        this.statistics = new ThreadStatisticsDefinition();
        this.context = new ThreadContextDefinition();
    }

    private UUID id;
    private UUID processID;
    private long status;
    private ThreadContextDefinition context;
    private ThreadStatisticsDefinition statistics;

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

    public long getStatus() {
        return this.status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public ThreadContextDefinition getContext() {
        return this.context;
    }

    public void setContext(ThreadContextDefinition context) {
        this.context = context;
    }

    public ThreadStatisticsDefinition getStatistics() {
        return this.statistics;
    }

    public void setStatistics(ThreadStatisticsDefinition statistics) {
        this.statistics = statistics;
    }

}
