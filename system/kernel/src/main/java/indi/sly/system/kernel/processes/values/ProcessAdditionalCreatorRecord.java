package indi.sly.system.kernel.processes.values;

public record ProcessAdditionalCreatorRecord(boolean inheritSession, long contextType) {
    public ProcessAdditionalCreatorRecord(long contextType) {
        this(true, contextType);
    }
}
