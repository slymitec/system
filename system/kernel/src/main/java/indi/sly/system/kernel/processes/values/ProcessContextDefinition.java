package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.values.ADefinition;
import indi.sly.system.kernel.sessions.values.AppContextDefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class ProcessContextDefinition extends ADefinition<ProcessContextDefinition> {
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
    private final List<IdentificationDefinition> workFolder;

    public AppContextDefinition getAppContext() {
        return this.appContext;
    }

    public void setAppContext(AppContextDefinition appContext) {
        if (ObjectUtil.isAnyNull(appContext)) {
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

    public List<IdentificationDefinition> getWorkFolder() {
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
        this.appContext = ObjectUtil.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.environmentVariable.put(StringUtil.readExternal(in), StringUtil.readExternal(in));
        }

        this.sessionID = UUIDUtil.readExternal(in);

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.parameters.put(StringUtil.readExternal(in), StringUtil.readExternal(in));
        }

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.workFolder.add(ObjectUtil.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        ObjectUtil.writeExternal(out, this.appContext);

        NumberUtil.writeExternalInteger(out, this.environmentVariable.size());
        for (Map.Entry<String, String> pair : this.environmentVariable.entrySet()) {
            StringUtil.writeExternal(out, pair.getKey());
            StringUtil.writeExternal(out, pair.getValue());
        }

        UUIDUtil.writeExternal(out, this.sessionID);

        NumberUtil.writeExternalInteger(out, this.parameters.size());
        for (Map.Entry<String, String> pair : this.parameters.entrySet()) {
            StringUtil.writeExternal(out, pair.getKey());
            StringUtil.writeExternal(out, pair.getValue());
        }

        NumberUtil.writeExternalInteger(out, this.workFolder.size());
        for (IdentificationDefinition pair : this.workFolder) {
            ObjectUtil.writeExternal(out, pair);
        }
    }
}
