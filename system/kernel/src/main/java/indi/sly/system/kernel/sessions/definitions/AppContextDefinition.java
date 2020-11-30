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
        this.parameters = new HashMap<>();
    }

    private UUID id;
    private String name;
    private long supportedSession;
    private String serverURL;
    private final Map<String, String> parameters;

    public UUID getID() {
        return this.id;
    }

    public void setID(UUID id) {
        this.id = id;
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

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public AppContextDefinition deepClone() {
        AppContextDefinition appContext = new AppContextDefinition();

        appContext.id = this.id;
        appContext.name = this.name;
        appContext.supportedSession = this.supportedSession;
        appContext.serverURL = this.serverURL;
        appContext.parameters.putAll(this.parameters);

        return appContext;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.id = UUIDUtils.readExternal(in);
        this.name = StringUtils.readExternal(in);
        this.supportedSession = NumberUtils.readExternalLong(in);
        this.serverURL = StringUtils.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.parameters.put(StringUtils.readExternal(in), StringUtils.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtils.writeExternal(out, this.id);
        StringUtils.writeExternal(out, this.name);
        NumberUtils.writeExternalLong(out, this.supportedSession);
        StringUtils.writeExternal(out, this.serverURL);

        NumberUtils.writeExternalInteger(out, this.parameters.size());
        for (Map.Entry<String, String> pair : this.parameters.entrySet()) {
            StringUtils.writeExternal(out, pair.getKey());
            StringUtils.writeExternal(out, pair.getValue());
        }
    }
}

