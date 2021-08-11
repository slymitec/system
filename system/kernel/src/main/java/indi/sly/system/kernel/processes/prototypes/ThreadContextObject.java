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
public class ThreadContextObject extends AValueProcessObject<ThreadContextDefinition> {
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

    public Map<String, Object> getParameters() {
        this.init();

        return CollectionUtil.unmodifiable(this.value.getRun().getParameters());
    }

    public void setParameters(Map<String, Object> parameters) {
        this.lock(LockType.WRITE);
        this.init();

        this.value.getRun().getParameters().clear();
        this.value.getRun().getParameters().putAll(parameters);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public Map<String, Object> getResults() {
        this.init();

        return CollectionUtil.unmodifiable(this.value.getRun().getResults());
    }

    public void setResults(Map<String, Object> results) {
        this.lock(LockType.WRITE);
        this.init();

        this.value.getRun().getResults().clear();
        this.value.getRun().getResults().putAll(results);

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
