package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.values.Identification;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.sessions.values.AppContextDefinition;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CreateProcessDefinition {
    private UUID fileHandle;

    private AccountAuthorizationObject accountAuthorization;
    private long privilegeTypes;
    private Map<Long, Integer> limits;

    private AppContextDefinition appContext;
    private Map<String, String> environmentVariable;
    private Map<String, String> parameters;
    private UUID sessionID;
    private List<Identification> workFolder;

    public UUID getFileHandle() {
        return this.fileHandle;
    }

    public void setFileHandle(UUID fileHandle) {
        this.fileHandle = fileHandle;
    }

    public AccountAuthorizationObject getAccountAuthorization() {
        return this.accountAuthorization;
    }

    public void setAccountAuthorization(AccountAuthorizationObject accountAuthorization) {
        this.accountAuthorization = accountAuthorization;
    }

    public long getPrivilegeTypes() {
        return this.privilegeTypes;
    }

    public void setPrivilegeTypes(long privilegeTypes) {
        this.privilegeTypes = privilegeTypes;
    }

    public Map<Long, Integer> getLimits() {
        return this.limits;
    }

    public void setLimits(Map<Long, Integer> limits) {
        this.limits = limits;
    }

    public AppContextDefinition getAppContext() {
        return this.appContext;
    }

    public void setAppContext(AppContextDefinition appContext) {
        this.appContext = appContext;
    }

    public Map<String, String> getEnvironmentVariable() {
        return this.environmentVariable;
    }

    public void setEnvironmentVariable(Map<String, String> environmentVariable) {
        this.environmentVariable = environmentVariable;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public UUID getSessionID() {
        return this.sessionID;
    }

    public void setSessionID(UUID sessionID) {
        this.sessionID = sessionID;
    }

    public List<Identification> getWorkFolder() {
        return this.workFolder;
    }

    public void setWorkFolder(List<Identification> workFolder) {
        this.workFolder = workFolder;
    }
}
