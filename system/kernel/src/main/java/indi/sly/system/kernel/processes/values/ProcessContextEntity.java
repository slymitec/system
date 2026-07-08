package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.core.values.APersistentEntity;

import java.util.*;

public class ProcessContextEntity extends APersistentEntity {
    public ProcessContextEntity() {
        this.environmentVariables = new HashMap<>();
    }

    private long type;
    private PathRecord path;
    private ApplicationRecord application;
    private final Map<String, String> environmentVariables;
    private String parameters;
    private PathRecord workFolder;

    public long getType() {
        return this.type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public ApplicationRecord getApplication() {
        return this.application;
    }

    public void setApplication(ApplicationRecord application) {
        if (ObjectUtil.isAnyNull(application)) {
            throw new ConditionParametersException();
        }

        this.application = application;
    }

    public PathRecord getPath() {
        return this.path;
    }

    public void setPath(PathRecord path) {
        this.path = path;
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

    public PathRecord getWorkFolder() {
        return workFolder;
    }

    public void setWorkFolder(PathRecord workFolder) {
        this.workFolder = workFolder;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProcessContextEntity that)) return false;
        return type == that.type && Objects.equals(path, that.path) && Objects.equals(application, that.application) && Objects.equals(environmentVariables, that.environmentVariables) && Objects.equals(parameters, that.parameters) && Objects.equals(workFolder, that.workFolder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, path, application, environmentVariables, parameters, workFolder);
    }
}
