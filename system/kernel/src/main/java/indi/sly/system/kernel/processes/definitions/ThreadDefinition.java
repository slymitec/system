package indi.sly.system.kernel.processes.definitions;

import java.util.UUID;

public class ThreadDefinition {
    public ThreadDefinition() {
        this.status = new ThreadStatisticsDefinition();
        this.context = new ThreadContextDefinition();
    }

    private UUID id;
    private UUID processID;
    private final ThreadStatisticsDefinition status;
    private final ThreadContextDefinition context;

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

    public ThreadStatisticsDefinition getStatus() {
        return this.status;
    }

    public ThreadContextDefinition getContext() {
        return this.context;
    }
}
