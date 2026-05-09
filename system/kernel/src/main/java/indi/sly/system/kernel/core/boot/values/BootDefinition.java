package indi.sly.system.kernel.core.boot.values;

import indi.sly.system.common.values.ADefinition;

public class BootDefinition extends ADefinition {
    protected long startupStatus;

    public long getStartupStatus() {
        return startupStatus;
    }

    public void setStartupStatus(long startupStatus) {
        this.startupStatus = startupStatus;
    }
}
