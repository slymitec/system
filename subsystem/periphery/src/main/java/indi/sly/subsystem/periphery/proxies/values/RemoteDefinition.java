package indi.sly.subsystem.periphery.proxies.values;

import indi.sly.system.common.values.ADefinition;
import org.jspecify.annotations.Nullable;

public class RemoteDefinition extends ADefinition {
    public RemoteDefinition() {
    }

    @SuppressWarnings("NullAway.Init")
    private CallContextRecord callContext;
    @Nullable
    private String taskName;
    @SuppressWarnings("NullAway.Init")
    private String value;

    public CallContextRecord getCallContext() {
        return this.callContext;
    }

    public void setCallContext(CallContextRecord callContext) {
        this.callContext = callContext;
    }

    public @Nullable String getTaskName() {
        return this.taskName;
    }

    public void setTaskName(@Nullable String taskName) {
        this.taskName = taskName;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
