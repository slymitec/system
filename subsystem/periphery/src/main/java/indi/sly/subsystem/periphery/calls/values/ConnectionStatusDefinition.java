package indi.sly.subsystem.periphery.calls.values;

import indi.sly.system.common.values.ADefinition;

public class ConnectionStatusDefinition extends ADefinition<ConnectionStatusDefinition> {
    public ConnectionStatusDefinition() {
    }

    private long runtime;
    private Object helper;

    public long getRuntime() {
        return this.runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    public Object getHelper() {
        return this.helper;
    }

    public void setHelper(Object helper) {
        this.helper = helper;
    }
}
