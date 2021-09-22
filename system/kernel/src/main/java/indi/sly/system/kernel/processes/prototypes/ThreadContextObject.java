package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AValueProcessObject;
import indi.sly.system.kernel.processes.values.ThreadContextDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadContextObject extends AValueProcessObject<ThreadContextDefinition, ThreadObject> {
    public long getType() {
        this.lock(LockType.READ);
        this.init();

        long type = this.value.getType();

        this.lock(LockType.NONE);
        return type;
    }

    public void setType(long type) {
        this.lock(LockType.WRITE);
        this.init();

        this.value.setType(type);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public Map<String, String> getParameters() {
        this.lock(LockType.READ);
        this.init();

        Map<String, String> parameters = this.value.getRun().getParameters();

        this.lock(LockType.NONE);
        return CollectionUtil.unmodifiable(parameters);
    }

    public void setParameters(Map<String, String> parameters) {
        this.lock(LockType.WRITE);
        this.init();

        this.value.getRun().getParameters().clear();
        this.value.getRun().getParameters().putAll(parameters);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public Map<String, String> getResults() {
        this.lock(LockType.READ);
        this.init();

        Map<String, String> results = this.value.getRun().getResults();

        this.lock(LockType.NONE);
        return CollectionUtil.unmodifiable(results);
    }

    public void setResults(Map<String, String> results) {
        this.lock(LockType.WRITE);
        this.init();

        this.value.getRun().getResults().clear();
        this.value.getRun().getResults().putAll(results);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public AKernelException getRunException() {
        this.lock(LockType.READ);
        this.init();

        AKernelException exception = this.value.getRun().getException();

        this.lock(LockType.NONE);
        return exception;
    }

    public void setRunException(AKernelException exception) {
        this.lock(LockType.WRITE);
        this.init();

        this.value.getRun().setException(exception);

        this.fresh();
        this.lock(LockType.NONE);
    }
}
