package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.kernel.core.prototypes.ACacheableObject;
import indi.sly.system.kernel.core.prototypes.AChildDefinitionObject;
import indi.sly.system.kernel.processes.values.ThreadContextDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.List;
import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadContextObject extends AChildDefinitionObject<ThreadContextDefinition, ThreadObject> {
    public long getType() {
        return this.definition.getType();
    }

    public void setType(long type) {
        this.definition.setType(type);
    }

    public ACacheableObject<?> getCacheableObject() {
        return this.definition.getRun().getCacheableObject();
    }

    public void setCacheableObject(ACacheableObject<?> cacheableObject) {
        this.definition.getRun().setCacheableObject(cacheableObject);
    }

    public List<String> getParameters() {
        List<String> parameters = this.definition.getRun().getParameters();

        return CollectionUtil.unmodifiable(parameters);
    }

    public void setParameters(List<String> parameters) {
        this.definition.getRun().getParameters().clear();
        this.definition.getRun().getParameters().addAll(parameters);
    }

    public Object getResult() {
        Object result = this.definition.getRun().getResult();

        return result;
    }

    public void setResult(Object result) {
        this.definition.getRun().setResult(result);
    }

    public AKernelException getRunException() {
        AKernelException exception = this.definition.getRun().getException();

        return exception;
    }

    public void setRunException(AKernelException exception) {
        this.definition.getRun().setException(exception);
    }
}
