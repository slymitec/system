package indi.sly.system.kernel.processes.instances.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

public class ClientDefinition extends ADefinition<ClientDefinition> {
    public ClientDefinition() {
        this.environment = new HashMap<>();
        this.paramaters = new HashMap<>();
    }

    private final Map<String, String> environment;
    private final Map<String, String> paramaters;

    public Map<String, String> getEnvironment() {
        return this.environment;
    }

    public Map<String, String> getParamaters() {
        return this.paramaters;
    }

    @Override
    public ClientDefinition deepClone() {
        ClientDefinition definition = new ClientDefinition();

        definition.environment.putAll(this.environment);
        definition.paramaters.putAll(this.paramaters);

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.environment.put(StringUtil.readExternal(in), StringUtil.readExternal(in));
        }

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.paramaters.put(StringUtil.readExternal(in), StringUtil.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtil.writeExternalInteger(out, this.environment.size());
        for (Map.Entry<String, String> pair : this.environment.entrySet()) {
            StringUtil.writeExternal(out, pair.getKey());
            StringUtil.writeExternal(out, pair.getValue());
        }

        NumberUtil.writeExternalInteger(out, this.paramaters.size());
        for (Map.Entry<String, String> pair : this.paramaters.entrySet()) {
            StringUtil.writeExternal(out, pair.getKey());
            StringUtil.writeExternal(out, pair.getValue());
        }
    }
}
