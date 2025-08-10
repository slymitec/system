package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AValueProcessObject;
import indi.sly.system.kernel.processes.values.ThreadContextDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadContextObject extends AValueProcessObject<ThreadContextDefinition, ThreadObject> {
    public long getType() {
        this.lock(LockType.READ);
        this.init();

        long type = this.value.getType();

        this.unlock(LockType.READ);
        return type;
    }

    public void setType(long type) {
        this.lock(LockType.WRITE);
        this.init();

        this.value.setType(type);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public Map<String, String> getParameters() {
        this.lock(LockType.READ);
        this.init();

        Map<String, String> parameters = this.value.getRun().getParameters();

        this.unlock(LockType.READ);
        return CollectionUtil.unmodifiable(parameters);
    }

    public void setParameters(Map<String, String> parameters) {
        this.lock(LockType.WRITE);
        this.init();

        this.value.getRun().getParameters().clear();
        this.value.getRun().getParameters().putAll(parameters);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public Object getResult() {
        this.lock(LockType.READ);
        this.init();

        Object result = this.value.getRun().getResult();

        this.unlock(LockType.READ);
        return result;
    }

    public void setResult(Object result) {
        this.lock(LockType.WRITE);
        this.init();

        this.value.getRun().setResult(result);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public AKernelException getRunException() {
        this.lock(LockType.READ);
        this.init();

        AKernelException exception = this.value.getRun().getException();

        this.unlock(LockType.READ);
        return exception;
    }

    public void setRunException(AKernelException exception) {
        this.lock(LockType.WRITE);
        this.init();

        this.value.getRun().setException(exception);

        this.fresh();
        this.unlock(LockType.WRITE);
    }
}
