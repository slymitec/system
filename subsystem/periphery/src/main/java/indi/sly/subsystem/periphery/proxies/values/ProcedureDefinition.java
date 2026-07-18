package indi.sly.subsystem.periphery.proxies.values;

import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProcedureDefinition extends ADefinition {
    public ProcedureDefinition() {
        this.handleTable = new HandleTableDefinition();
    }

    private String call;
    private ProcedureProcessRecord process;
    private final HandleTableDefinition handleTable;

    public String getCall() {
        return this.call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public ProcedureProcessRecord getProcess() {
        return this.process;
    }

    public void setProcess(ProcedureProcessRecord process) {
        this.process = process;
    }

    public HandleTableDefinition getHandleTable() {
        return this.handleTable;
    }
}
