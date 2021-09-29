package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ApplicationDefinition extends ADefinition<ApplicationDefinition> {
    public ApplicationDefinition() {
        this.configurations = new HashMap<>();
    }

    private UUID id;
    private String name;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationDefinition that = (ApplicationDefinition) o;
        return supportedSession == that.supportedSession && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(serverURL, that.serverURL) && configurations.equals(that.configurations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, supportedSession, serverURL, configurations);
    }

    @Override
    public ApplicationDefinition deepClone() {
        ApplicationDefinition definition = new ApplicationDefinition();

        definition.id = this.id;
        definition.name = this.name;
        definition.supportedSession = this.supportedSession;
        definition.serverURL = this.serverURL;
        definition.configurations.putAll(this.configurations);

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        this.id = UUIDUtil.readExternal(in);
        this.name = StringUtil.readExternal(in);
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
        NumberUtil.writeExternalLong(out, this.supportedSession);
        StringUtil.writeExternal(out, this.serverURL);

        NumberUtil.writeExternalInteger(out, this.configurations.size());
        for (Map.Entry<String, String> pair : this.configurations.entrySet()) {
            StringUtil.writeExternal(out, pair.getKey());
            StringUtil.writeExternal(out, pair.getValue());
        }
    }
}

