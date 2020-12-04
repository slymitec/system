package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.StringUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.sessions.values.AppContextDefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class ProcessContextDefinition implements ISerializable<ProcessContextDefinition> {
    public ProcessContextDefinition() {
        this.appContext = new AppContextDefinition();
        this.environmentVariable = new HashMap<>();
        this.parameters = new HashMap<>();
        this.workFolder = new ArrayList<>();
    }

    private AppContextDefinition appContext;
    private final Map<String, String> environmentVariable;
    private final Map<String, String> parameters;
    private UUID sessionID;
    private final List<Identification> workFolder;

    public AppContextDefinition getAppContext() {
        return this.appContext;
    }

    public void setAppContext(AppContextDefinition appContext) {
        if (ObjectUtils.isAnyNull(appContext)) {
            throw new ConditionParametersException();
        }

        this.appContext = appContext;
    }

    public Map<String, String> getEnvironmentVariable() {
        return this.environmentVariable;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public ProcessContextDefinition deepClone() {
        ProcessContextDefinition definition = new ProcessContextDefinition();

        definition.appContext = this.appContext.deepClone();
        definition.environmentVariable.putAll(this.environmentVariable);
        definition.parameters.putAll(this.parameters);
        definition.sessionID = this.sessionID;
        definition.workFolder.addAll(this.workFolder);

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.appContext = ObjectUtils.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.environmentVariable.put(StringUtils.readExternal(in), StringUtils.readExternal(in));
        }

        this.sessionID = UUIDUtils.readExternal(in);

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.parameters.put(StringUtils.readExternal(in), StringUtils.readExternal(in));
        }

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.workFolder.add(ObjectUtils.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        ObjectUtils.writeExternal(out, this.appContext);

        NumberUtils.writeExternalInteger(out, this.environmentVariable.size());
        for (Map.Entry<String, String> pair : this.environmentVariable.entrySet()) {
            StringUtils.writeExternal(out, pair.getKey());
            StringUtils.writeExternal(out, pair.getValue());
        }

        UUIDUtils.writeExternal(out, this.sessionID);

        NumberUtils.writeExternalInteger(out, this.parameters.size());
        for (Map.Entry<String, String> pair : this.parameters.entrySet()) {
            StringUtils.writeExternal(out, pair.getKey());
            StringUtils.writeExternal(out, pair.getValue());
        }

        NumberUtils.writeExternalInteger(out, this.workFolder.size());
        for (Identification pair : this.workFolder) {
            ObjectUtils.writeExternal(out, pair);
        }
    }
}
