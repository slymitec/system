package indi.sly.system.kernel.processes.prototypes;

public class ThreadContextDefinition {
    private String command;
    private int offset;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
