package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class ProcessContextDefinition extends ADefinition<ProcessContextDefinition> {
    public ProcessContextDefinition() {
        this.identifications = new ArrayList<>();
        this.environmentVariables = new HashMap<>();
        this.workFolder = new ArrayList<>();
    }

    private long type;
    private final List<IdentificationDefinition> identifications;
    private ApplicationDefinition application;
    private final Map<String, String> environmentVariables;
    private String parameters;
    private final List<IdentificationDefinition> workFolder;

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

    public List<IdentificationDefinition> getIdentifications() {
        return this.identifications;
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

    public List<IdentificationDefinition> getWorkFolder() {
        return this.workFolder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessContextDefinition that = (ProcessContextDefinition) o;
        return type == that.type && identifications.equals(that.identifications) && Objects.equals(application, that.application) && environmentVariables.equals(that.environmentVariables) && Objects.equals(parameters, that.parameters) && workFolder.equals(that.workFolder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, identifications, application, environmentVariables, parameters, workFolder);
    }

    @Override
    public ProcessContextDefinition deepClone() {
        ProcessContextDefinition definition = new ProcessContextDefinition();

        definition.type = this.type;
        definition.identifications.addAll(this.identifications);
        definition.application = this.application.deepClone();
        definition.environmentVariables.putAll(this.environmentVariables);
        definition.parameters = this.parameters;
        definition.workFolder.addAll(this.workFolder);

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        this.type = NumberUtil.readExternalLong(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.identifications.add(ObjectUtil.readExternal(in));
        }

        this.application = ObjectUtil.readExternal(in);

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.environmentVariables.put(StringUtil.readExternal(in), StringUtil.readExternal(in));
        }

        this.parameters = StringUtil.readExternal(in);

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.workFolder.add(ObjectUtil.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        NumberUtil.writeExternalLong(out, this.type);

        NumberUtil.writeExternalInteger(out, this.identifications.size());
        for (IdentificationDefinition pair : this.identifications) {
            ObjectUtil.writeExternal(out, pair);
        }

        ObjectUtil.writeExternal(out, this.application);

        NumberUtil.writeExternalInteger(out, this.environmentVariables.size());
        for (Map.Entry<String, String> pair : this.environmentVariables.entrySet()) {
            StringUtil.writeExternal(out, pair.getKey());
            StringUtil.writeExternal(out, pair.getValue());
        }

        StringUtil.writeExternal(out, this.parameters);

        NumberUtil.writeExternalInteger(out, this.workFolder.size());
        for (IdentificationDefinition pair : this.workFolder) {
            ObjectUtil.writeExternal(out, pair);
        }
    }
}
