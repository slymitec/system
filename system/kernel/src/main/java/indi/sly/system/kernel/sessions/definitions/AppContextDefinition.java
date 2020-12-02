package indi.sly.system.kernel.sessions.definitions;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.StringUtils;
import indi.sly.system.common.utility.UUIDUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AppContextDefinition implements ISerializable<AppContextDefinition> {
    public AppContextDefinition() {
        this.configurations = new HashMap<>();
    }

    private UUID processID;
    private String name;
    private long supportedSession;
    private String serverURL;
    private final Map<String, String> configurations;

    public UUID getProcessID() {
        return this.processID;
    }

    public void setProcessID(UUID id) {
        this.processID = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSupportedSession() {
        return this.supportedSession;
    }

    public void setSupportedSession(long supportedSession) {
        this.supportedSession = supportedSession;
    }

    public String getServerURL() {
        return this.serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public Map<String, String> getConfigurations() {
        return this.configurations;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public AppContextDefinition deepClone() {
        AppContextDefinition appContext = new AppContextDefinition();

        appContext.processID = this.processID;
        appContext.name = this.name;
        appContext.supportedSession = this.supportedSession;
        appContext.serverURL = this.serverURL;
        appContext.configurations.putAll(this.configurations);

        return appContext;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.processID = UUIDUtils.readExternal(in);
        this.name = StringUtils.readExternal(in);
        this.supportedSession = NumberUtils.readExternalLong(in);
        this.serverURL = StringUtils.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.configurations.put(StringUtils.readExternal(in), StringUtils.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtils.writeExternal(out, this.processID);
        StringUtils.writeExternal(out, this.name);
        NumberUtils.writeExternalLong(out, this.supportedSession);
        StringUtils.writeExternal(out, this.serverURL);

        NumberUtils.writeExternalInteger(out, this.configurations.size());
        for (Map.Entry<String, String> pair : this.configurations.entrySet()) {
            StringUtils.writeExternal(out, pair.getKey());
            StringUtils.writeExternal(out, pair.getValue());
        }
    }
}

