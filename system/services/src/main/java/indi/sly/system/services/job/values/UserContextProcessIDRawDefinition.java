package indi.sly.system.services.job.values;

import indi.sly.system.common.values.ADefinition;

import java.util.UUID;

public class UserContextProcessIDRawDefinition extends ADefinition<UserContextProcessIDRawDefinition> {
    private UUID processID;
    private String verification;

    public UUID getProcessID() {
        return this.processID;
    }

    public void setProcessID(UUID processID) {
        this.processID = processID;
    }

    public String getVerification() {
        return this.verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }
}
