package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.values.ADefinition;
import indi.sly.system.kernel.core.prototypes.ACacheableObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreadRunDefinition extends ADefinition {
    public ThreadRunDefinition() {
        this.parameters = new ArrayList<>();
    }

    private ACacheableObject<?> cacheableObject;
    private final List<String> parameters;
    private Object result;
    private AKernelException exception;

    public ACacheableObject<?> getCacheableObject() {
        return this.cacheableObject;
    }

    public void setCacheableObject(ACacheableObject<?> cacheableObject) {
        this.cacheableObject = cacheableObject;
    }

    public List<String> getParameters() {
        return this.parameters;
    }

    public Object getResult() {
        return this.result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public AKernelException getException() {
        return this.exception;
    }

    public void setException(AKernelException exception) {
        this.exception = exception;
    }

}