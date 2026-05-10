package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;

import java.util.UUID;

public class ClientRequestProcessIdDefinition extends ADefinition {
    private UUID id;
    private String verification;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getVerification() {
        return this.verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }
}
