package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;

public class ThreadRunDefinition extends ADefinition<ThreadRunDefinition> {
    public ThreadRunDefinition() {
        this.data = new HashMap<>();
    }

    private final Map<String, ISerializeCapable> data;
    private AKernelException exception;

    public Map<String, ISerializeCapable> getData() {
        return this.data;
    }

    public AKernelException getException() {
        return this.exception;
    }

    public void setException(AKernelException exception) {
        this.exception = exception;
    }
}
