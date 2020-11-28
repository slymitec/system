package indi.sly.system.kernel.sessions.prototypes.instances;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.kernel.processes.prototypes.ProcessContextDefinition;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenDefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LogDefinition implements ISerializable<LogDefinition> {
    public LogDefinition() {
        this.value = new HashMap<>();
    }

    private UUID processID;
    private ProcessTokenDefinition processToken;
    private ProcessContextDefinition processContext;
    private final Map<String, String> value;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

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
