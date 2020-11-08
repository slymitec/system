package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.kernel.core.enviroment.UserSpace;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class ThreadDefinition {
    public ThreadDefinition() {
        this.context = new ThreadContextDefinition();
    }

    private UUID id;
    private UUID processID;
    private Map<Long, Date> date;
    private UserSpace userSpace;
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

    public Map<Long, Date> getDate() {
        return this.date;
    }

    public void setDate(Map<Long, Date> date) {
        this.date = date;
    }

    public UserSpace getUserSpace() {
        return this.userSpace;
    }

    public void setUserSpace(UserSpace userSpace) {
        this.userSpace = userSpace;
    }

    public ThreadContextDefinition getContext() {
        return this.context;
    }
}
