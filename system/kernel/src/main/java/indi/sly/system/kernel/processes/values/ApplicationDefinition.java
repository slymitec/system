package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ApplicationDefinition extends ADefinition {
    public ApplicationDefinition() {
        this.configurations = new HashMap<>();
    }

    private UUID id;
    private String name;
    private String serverURL;
    private final Map<String, String> configurations;

    public UUID getId() {
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
    public final boolean equals(Object o) {
        if (!(o instanceof ApplicationDefinition that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(serverURL, that.serverURL) && Objects.equals(configurations, that.configurations);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id, name, serverURL, configurations);
    }
}

