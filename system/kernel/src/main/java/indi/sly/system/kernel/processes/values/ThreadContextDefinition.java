package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.values.ADefinition;

public class ThreadContextDefinition extends ADefinition<ThreadContextDefinition> {
    public ThreadContextDefinition() {
        this.type = ThreadContextType.NULL;
        this.run = new ThreadRunDefinition();
    }

    private long type;
    private final ThreadRunDefinition run;

    public long getType() {
        return this.type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public ThreadRunDefinition getRun() {
        return this.run;
    }
}
