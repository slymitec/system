package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;

import java.util.List;
import java.util.UUID;

public class ProcessCreatorDefinition extends ADefinition<ProcessCreatorDefinition> {
    public ProcessCreatorDefinition() {
        this.inheritSessionID = true;
    }

    private AccountAuthorizationObject accountAuthorization;
    private boolean inheritSessionID;
    private UUID fileIndex;
    private String parameters;
    private List<IdentificationDefinition> workFolder;

    public AccountAuthorizationObject getAccountAuthorization() {
        return this.accountAuthorization;
    }

    public void setAccountAuthorization(AccountAuthorizationObject accountAuthorization) {
        this.accountAuthorization = accountAuthorization;
    }

    public boolean isInheritSessionID() {
        return inheritSessionID;
    }

    public void setInheritSessionID(boolean inheritSessionID) {
        this.inheritSessionID = inheritSessionID;
    }

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
}
