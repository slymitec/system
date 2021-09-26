package indi.sly.system.controllers.values;

import indi.sly.system.common.values.ADefinition;

import java.util.UUID;

public class UserContentRequestDefinition extends ADefinition<UserContentRequestDefinition> {
    private UUID processID;

    public UUID getProcessID() {
        return this.processID;
    }

    public void setProcessID(UUID processID) {
        this.processID = processID;
    }


}
