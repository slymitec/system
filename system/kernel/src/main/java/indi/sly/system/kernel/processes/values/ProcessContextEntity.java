package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.values.APersistentEntity;

import java.util.*;

public class ProcessContextEntity extends APersistentEntity {
    public ProcessContextEntity() {
        this.environmentVariables = new HashMap<>();
    }

    private long type;
    private PathDefinition path;
    private ApplicationDefinition application;
    private final Map<String, String> environmentVariables;
    private String parameters;
    private PathDefinition workFolder;

    public long getType() {
        return this.type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public ApplicationDefinition getApplication() {
        return this.application;
    }

    public void setApplication(ApplicationDefinition application) {
        if (ObjectUtil.isAnyNull(application)) {
            throw new ConditionParametersException();
        }

        this.application = application;
    }

    public PathDefinition getPath() {
        return this.path;
    }

    public void setPath(PathDefinition path) {
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

    public PathDefinition getWorkFolder() {
        return workFolder;
    }

    public void setWorkFolder(PathDefinition workFolder) {
        this.workFolder = workFolder;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProcessContextEntity that = (ProcessContextEntity) o;
        return type == that.type && Objects.equals(path, that.path) && Objects.equals(application, that.application) && Objects.equals(environmentVariables, that.environmentVariables) && Objects.equals(parameters, that.parameters) && Objects.equals(workFolder, that.workFolder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, path, application, environmentVariables, parameters, workFolder);
    }
}
