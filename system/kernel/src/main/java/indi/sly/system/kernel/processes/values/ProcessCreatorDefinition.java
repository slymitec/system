package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;

import java.util.UUID;

public class ProcessCreatorDefinition extends ADefinition {
    public ProcessCreatorDefinition() {
        this.inheritSession = true;
        this.contextType = ProcessContextType.EXECUTABLE;
    }

    private AccountAuthorizationObject accountAuthorization;
    private boolean inheritSession;
    private long contextType;
    private UUID fileIndex;
    private String parameters;
    private PathRecord workFolder;

    public AccountAuthorizationObject getAccountAuthorization() {
        return this.accountAuthorization;
    }

    public void setAccountAuthorization(AccountAuthorizationObject accountAuthorization) {
        this.accountAuthorization = accountAuthorization;
    }

    public boolean isInheritSession() {
        return inheritSession;
    }

    public void setInheritSession(boolean inheritSession) {
        this.inheritSession = inheritSession;
    }

    public long getContextType() {
        return this.contextType;
    }

    public void setContextType(long contextType) {
        this.contextType = contextType;
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

    public PathRecord getWorkFolder() {
        return this.workFolder;
    }

    public void setWorkFolder(PathRecord workFolder) {
        this.workFolder = workFolder;
    }
}
