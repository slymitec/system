package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ThreadContextDefinition extends ADefinition<ThreadContextDefinition> {
    public ThreadContextDefinition() {
        this.run = new ThreadRunDefinition();
    }

    private ThreadRunDefinition run;

    public ThreadRunDefinition getRun() {
        return this.run;
    }

    @Override
    public ThreadContextDefinition deepClone() {
        ThreadContextDefinition definition = new ThreadContextDefinition();

        definition.run = this.run.deepClone();

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.run = ObjectUtil.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        ObjectUtil.writeExternal(out, this.run);
    }
}
