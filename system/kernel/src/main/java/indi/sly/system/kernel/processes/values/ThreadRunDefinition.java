package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;

public class ThreadRunDefinition extends ADefinition<ThreadRunDefinition> {
    public ThreadRunDefinition() {
        this.parameters = new HashMap<>();
        this.results = new HashMap<>();
    }

    private final Map<String, String> parameters;
    private final Map<String, String> results;
    private AKernelException exception;

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public Map<String, String> getResults() {
        return this.results;
    }

    public AKernelException getException() {
        return this.exception;
    }

    public void setException(AKernelException exception) {
        this.exception = exception;
    }
}
