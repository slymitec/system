package indi.sly.subsystem.periphery.calls.values;

import indi.sly.system.common.values.ADefinition;

public class ClientResponseExceptionTraceDefinition extends ADefinition {
    private String clazz;
    private String method;

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
