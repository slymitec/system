package indi.sly.system.kernel.sessions.values;

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
        this.roles = new HashSet<>();
    }

    private String name;
    private long supportedSession;
    private String serverURL;
    private final Map<String, String> configurations;
    private final Set<UUID> roles;

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

    public Set<UUID> getRoles() {
        return this.roles;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public AppContextDefinition deepClone() {
        AppContextDefinition definition = new AppContextDefinition();

        definition.name = this.name;
        definition.supportedSession = this.supportedSession;
        definition.serverURL = this.serverURL;
        definition.configurations.putAll(this.configurations);
        definition.roles.addAll(this.roles);

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.name = StringUtil.readExternal(in);
        this.supportedSession = NumberUtil.readExternalLong(in);
        this.serverURL = StringUtil.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.configurations.put(StringUtil.readExternal(in), StringUtil.readExternal(in));
        }

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.roles.add(UUIDUtil.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        StringUtil.writeExternal(out, this.name);
        NumberUtil.writeExternalLong(out, this.supportedSession);
        StringUtil.writeExternal(out, this.serverURL);

        NumberUtil.writeExternalInteger(out, this.configurations.size());
        for (Map.Entry<String, String> pair : this.configurations.entrySet()) {
            StringUtil.writeExternal(out, pair.getKey());
            StringUtil.writeExternal(out, pair.getValue());
        }

        NumberUtil.writeExternalInteger(out, this.roles.size());
        for (UUID pair : this.roles) {
            UUIDUtil.writeExternal(out, pair);
        }
    }
}

