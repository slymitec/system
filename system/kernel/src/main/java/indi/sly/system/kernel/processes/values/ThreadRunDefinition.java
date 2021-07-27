package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.values.ADefinition;

public class ThreadRunDefinition extends ADefinition<ThreadRunDefinition> {
    private ISerializeCapable<?>[] arguments;
    private ISerializeCapable<?>[] results;
    private AKernelException exception;

    public Object[] getArguments() {
        return this.arguments;
    }

    public void setArguments(ISerializeCapable<?>[] arguments) {
        this.arguments = arguments;
    }

    public ISerializeCapable<?>[] getResults() {
        return this.results;
    }

    public void setResults(ISerializeCapable<?>[] results) {
        this.results = results;
    }

    public AKernelException getException() {
        return this.exception;
    }

    public void setException(AKernelException exception) {
        this.exception = exception;
    }
}
