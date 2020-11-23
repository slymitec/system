package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.StringUtils;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.sessions.prototypes.AppContextDefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessContextDefinition implements ISerializable<ProcessContextDefinition> {
    public ProcessContextDefinition() {
        this.environmentVariable = new HashMap<>();
        this.workFolder = new ArrayList<>();
        this.appContext = new AppContextDefinition();
    }

    private final Map<String, String> environmentVariable;
    private final List<Identification> workFolder;
    private AppContextDefinition appContext;

    public Map<String, String> getEnvironmentVariable() {
        return this.environmentVariable;
    }

    public List<Identification> getWorkFolder() {
        return this.workFolder;
    }

    public AppContextDefinition getAppContext() {
        return this.appContext;
    }

    public void setAppContext(AppContextDefinition appContext) {
        this.appContext = appContext;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public ProcessContextDefinition deepClone() {
        ProcessContextDefinition processContext = new ProcessContextDefinition();

        processContext.environmentVariable.putAll(this.environmentVariable);
        processContext.workFolder.addAll(this.workFolder);
        processContext.appContext = this.appContext.deepClone();

        return processContext;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int valueInteger;

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.environmentVariable.put(StringUtils.readExternal(in), StringUtils.readExternal(in));
        }

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.workFolder.add(ObjectUtils.readExternal(in));
        }

        this.appContext = ObjectUtils.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtils.writeExternalInteger(out, this.environmentVariable.size());
        for (Map.Entry<String, String> pair : this.environmentVariable.entrySet()) {
            StringUtils.writeExternal(out, pair.getKey());
            StringUtils.writeExternal(out, pair.getValue());
        }

        NumberUtils.writeExternalInteger(out, this.workFolder.size());
        for (Identification pair : this.workFolder) {
            ObjectUtils.writeExternal(out, pair);
        }

        ObjectUtils.writeExternal(out, this.appContext);
    }
}
