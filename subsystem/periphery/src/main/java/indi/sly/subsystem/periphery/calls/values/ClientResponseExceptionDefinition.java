package indi.sly.subsystem.periphery.calls.values;

import indi.sly.system.common.values.ADefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientResponseExceptionDefinition extends ADefinition {
    public ClientResponseExceptionDefinition() {
        trace = new ArrayList<>();
    }

    private UUID id;
    private String clazz;
    private final List<ClientResponseExceptionTraceDefinition> trace;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public List<ClientResponseExceptionTraceDefinition> getTrace() {
        return this.trace;
    }
}
