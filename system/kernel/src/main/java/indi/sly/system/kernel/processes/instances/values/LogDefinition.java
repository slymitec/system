package indi.sly.system.kernel.processes.instances.values;

import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.kernel.processes.values.ProcessContextDefinition;
import indi.sly.system.kernel.processes.values.ProcessTokenDefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LogDefinition implements ISerializeCapable<LogDefinition> {
    public LogDefinition() {
        this.value = new HashMap<>();
    }

    private UUID processID;
    private ProcessTokenDefinition processToken;
    private ProcessContextDefinition processContext;
    private final Map<String, String> value;

    @Override
    public LogDefinition deepClone() {
        return null;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }

}
