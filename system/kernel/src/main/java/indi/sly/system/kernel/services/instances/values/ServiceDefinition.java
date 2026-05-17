package indi.sly.system.kernel.services.instances.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.common.values.PathDefinition;

import java.util.*;

public class ServiceDefinition extends ADefinition {
    public ServiceDefinition() {
        this.dependencies = new ArrayList<>();
        this.environmentVariables = new HashMap<>();
    }

    private final List<UUID> dependencies;
    private String secret;
    private PathDefinition path;
    private UUID accountId;
    private long mode;
    private long start;
    private final Map<String, String> environmentVariables;
    private String parameters;

    public List<UUID> getDependencies() {
        return this.dependencies;
    }

    public String getSecret() {
        return this.secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public PathDefinition getPath() {
        return this.path;
    }

    public void setPath(PathDefinition path) {
        this.path = path;
    }

    public UUID getAccountId() {
        return this.accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public long getMode() {
        return this.mode;
    }

    public void setMode(long mode) {
        this.mode = mode;
    }

    public long getStart() {
        return this.start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public Map<String, String> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    public String getParameters() {
        return this.parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ServiceDefinition that = (ServiceDefinition) o;
        return mode == that.mode && start == that.start && Objects.equals(dependencies, that.dependencies) && Objects.equals(secret, that.secret) && Objects.equals(path, that.path) && Objects.equals(accountId, that.accountId) && Objects.equals(environmentVariables, that.environmentVariables) && Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dependencies, secret, path, accountId, mode, start, environmentVariables, parameters);
    }
}
