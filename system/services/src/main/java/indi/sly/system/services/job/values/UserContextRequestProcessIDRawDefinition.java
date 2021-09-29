package indi.sly.system.services.job.values;

import indi.sly.system.common.values.ADefinition;

import java.util.UUID;

public class UserContextRequestProcessIDRawDefinition extends ADefinition<UserContextRequestProcessIDRawDefinition> {
    private UUID id;
    private String verification;

    public UUID getID() {
        return this.id;
    }

    public void setID(UUID id) {
        this.id = id;
    }

    public String getVerification() {
        return this.verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }
}
