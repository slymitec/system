package indi.sly.system.kernel.sessions.values;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.StringUtils;
import indi.sly.system.common.utility.UUIDUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class AppContextDefinition implements ISerializable<AppContextDefinition> {
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
        this.name = StringUtils.readExternal(in);
        this.supportedSession = NumberUtils.readExternalLong(in);
        this.serverURL = StringUtils.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.configurations.put(StringUtils.readExternal(in), StringUtils.readExternal(in));
        }

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.roles.add(UUIDUtils.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        StringUtils.writeExternal(out, this.name);
        NumberUtils.writeExternalLong(out, this.supportedSession);
        StringUtils.writeExternal(out, this.serverURL);

        NumberUtils.writeExternalInteger(out, this.configurations.size());
        for (Map.Entry<String, String> pair : this.configurations.entrySet()) {
            StringUtils.writeExternal(out, pair.getKey());
            StringUtils.writeExternal(out, pair.getValue());
        }

        NumberUtils.writeExternalInteger(out, this.roles.size());
        for (UUID pair : this.roles) {
            UUIDUtils.writeExternal(out, pair);
        }
    }
}

