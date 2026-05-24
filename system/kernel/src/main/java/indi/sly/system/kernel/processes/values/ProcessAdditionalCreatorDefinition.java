package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.values.ADefinition;

import java.util.Objects;

public class ProcessAdditionalCreatorDefinition extends ADefinition {
    public ProcessAdditionalCreatorDefinition() {
        this.inheritSession = true;
        this.contextType = ProcessContextType.EXECUTABLE;
    }

    private boolean inheritSession;
    private long contextType;

    public boolean isInheritSession() {
        return inheritSession;
    }

    public void setInheritSession(boolean inheritSession) {
        this.inheritSession = inheritSession;
    }

    public long getContextType() {
        return this.contextType;
    }

    public void setContextType(long contextType) {
        this.contextType = contextType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProcessAdditionalCreatorDefinition that = (ProcessAdditionalCreatorDefinition) o;
        return inheritSession == that.inheritSession && contextType == that.contextType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(inheritSession, contextType);
    }
}
