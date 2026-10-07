package indi.sly.subsystem.periphery.proxies.values;

import indi.sly.system.common.values.ADefinition;

public class RemoteDefinition extends ADefinition {
    public RemoteDefinition() {
    }

    private CallContextRecord callContext;
    private String taskName;
    private String value;

    public CallContextRecord getCallContext() {
        return this.callContext;
    }

    public void setCallContext(CallContextRecord callContext) {
        this.callContext = callContext;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
