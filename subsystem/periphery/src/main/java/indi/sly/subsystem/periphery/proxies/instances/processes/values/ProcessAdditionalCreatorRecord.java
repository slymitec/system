package indi.sly.subsystem.periphery.proxies.instances.processes.values;

public record ProcessAdditionalCreatorRecord(boolean inheritSession, long contextType) {
    public ProcessAdditionalCreatorRecord(long contextType) {
        this(true, contextType);
    }
}
