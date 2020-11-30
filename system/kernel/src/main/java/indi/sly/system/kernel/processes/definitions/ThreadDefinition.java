package indi.sly.system.kernel.processes.definitions;

import indi.sly.system.kernel.core.enviroment.UserSpace;

import java.util.UUID;

public class ThreadDefinition {
    public ThreadDefinition() {
        this.userSpace = new UserSpace();
        this.status = new ThreadStatusDefinition();
        this.context = new ThreadContextDefinition();

    }

    private UUID id;
    private UUID processID;
    private final UserSpace userSpace;
    private final ThreadStatusDefinition status;
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

    public UserSpace getUserSpace() {
        return this.userSpace;
    }

    public ThreadStatusDefinition getStatus() {
        return this.status;
    }

    public ThreadContextDefinition getContext() {
        return this.context;
    }
}
