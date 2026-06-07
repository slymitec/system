package indi.sly.subsystem.periphery.proxies.values;

import indi.sly.subsystem.periphery.core.values.ACacheEntity;

import java.util.UUID;

public class ProxyContextCacheEntity extends ACacheEntity {
    private String call;
    private UUID processId;
    private long type;
    private String secret;
    private String verification;

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public UUID getProcessId() {
        return processId;
    }

    public void setProcessId(UUID processId) {
        this.processId = processId;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }
}
