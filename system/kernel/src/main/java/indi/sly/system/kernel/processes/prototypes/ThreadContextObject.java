package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AValueProcessPrototype;
import indi.sly.system.kernel.processes.values.ThreadContextDefinition;

public class ThreadContextObject extends AValueProcessPrototype<ThreadContextDefinition> {
    protected ProcessObject process;

    public long getType() {
        this.init();

        return this.value.getType();
    }

    public void setType(long type) {
        this.lock(LockType.WRITE);
        this.init();

        this.value.setType(type);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public Object[] getRunArguments() {
        this.init();

        return this.value.getRun().getArguments();
    }

    public void setRunArguments(ISerializeCapable<?>[] arguments) {
        this.lock(LockType.WRITE);
        this.init();

        this.value.getRun().setArguments(arguments);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public ISerializeCapable<?>[] getRunResults() {
        this.init();

        return this.value.getRun().getResults();
    }

    public void setRunResults(ISerializeCapable<?>[] results) {
        this.lock(LockType.WRITE);
        this.init();

        this.value.getRun().setResults(results);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public AKernelException getRunException() {
        this.init();

        return this.value.getRun().getException();
    }

    public void setRunException(AKernelException exception) {
        this.lock(LockType.WRITE);
        this.init();

        this.value.getRun().setException(exception);

        this.fresh();
        this.lock(LockType.NONE);
    }
}
