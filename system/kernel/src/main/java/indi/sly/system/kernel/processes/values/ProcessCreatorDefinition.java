package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ProcessCreatorDefinition extends ADefinition<ProcessCreatorDefinition> {
    private UUID fileIndex;

    private String parameters;
    private List<IdentificationDefinition> workFolder;

    private AccountAuthorizationObject accountAuthorization;
    private Set<UUID> additionalRoles;
    private Map<Long, Integer> limits;
    private long privileges;

    public UUID getFileIndex() {
        return this.fileIndex;
    }

    public void setFileIndex(UUID fileIndex) {
        this.fileIndex = fileIndex;
    }

    public String getParameters() {
        return this.parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public List<IdentificationDefinition> getWorkFolder() {
        return this.workFolder;
    }

    public void setWorkFolder(List<IdentificationDefinition> workFolder) {
        this.workFolder = workFolder;
    }

    public AccountAuthorizationObject getAccountAuthorization() {
        return this.accountAuthorization;
    }

    public void setAccountAuthorization(AccountAuthorizationObject accountAuthorization) {
        this.accountAuthorization = accountAuthorization;
    }

    public Set<UUID> getAdditionalRoles() {
        return this.additionalRoles;
    }

    public void setAdditionalRoles(Set<UUID> additionalRoles) {
        this.additionalRoles = additionalRoles;
    }

    public Map<Long, Integer> getLimits() {
        return this.limits;
    }

    public void setLimits(Map<Long, Integer> limits) {
        this.limits = limits;
    }

    public long getPrivileges() {
        return this.privileges;
    }

    public void setPrivileges(long privileges) {
        this.privileges = privileges;
    }
}
