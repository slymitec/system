package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class AppContextDefinition implements ISerializeCapable<AppContextDefinition> {
    public AppContextDefinition() {
        this.configurations = new HashMap<>();
    }

    private UUID id;
    private String name;
    private long type;
    private long supportedSession;
    private String serverURL;
    private final Map<String, String> configurations;

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

    public long getType() {
        return this.type;
    }

    public void setType(long type) {
        this.type = type;
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
        AppContextDefinition definition = new AppContextDefinition();

        definition.id = this.id;
        definition.name = this.name;
        definition.type = this.type;
        definition.supportedSession = this.supportedSession;
        definition.serverURL = this.serverURL;
        definition.configurations.putAll(this.configurations);

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.id = UUIDUtil.readExternal(in);
        this.name = StringUtil.readExternal(in);
        this.type = NumberUtil.readExternalLong(in);
        this.supportedSession = NumberUtil.readExternalLong(in);
        this.serverURL = StringUtil.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.configurations.put(StringUtil.readExternal(in), StringUtil.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtil.writeExternal(out, this.id);
        StringUtil.writeExternal(out, this.name);
        NumberUtil.writeExternalLong(out, this.type);
        NumberUtil.writeExternalLong(out, this.supportedSession);
        StringUtil.writeExternal(out, this.serverURL);

        NumberUtil.writeExternalInteger(out, this.configurations.size());
        for (Map.Entry<String, String> pair : this.configurations.entrySet()) {
            StringUtil.writeExternal(out, pair.getKey());
            StringUtil.writeExternal(out, pair.getValue());
        }
    }
}

