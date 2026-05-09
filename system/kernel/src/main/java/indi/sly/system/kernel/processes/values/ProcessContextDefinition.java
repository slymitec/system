package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.values.IdentifierDefinition;
import indi.sly.system.common.values.ADefinition;
import indi.sly.system.common.values.PathDefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class ProcessContextDefinition extends ADefinition {
    public ProcessContextDefinition() {
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
}
