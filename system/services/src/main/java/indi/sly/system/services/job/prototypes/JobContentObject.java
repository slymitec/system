package indi.sly.system.services.job.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.*;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.core.prototypes.CoreObjectRepositoryObject;
import indi.sly.system.kernel.processes.prototypes.ThreadContextObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobContentObject extends AObject {
    protected ThreadContextObject threadContext;

    public Set<String> getParameterNames() {
        Map<String, Object> threadContextData = this.threadContext.getParameters();

        return CollectionUtil.unmodifiable(threadContextData.keySet());
    }

    public Set<String> getResultNames() {
        Map<String, Object> threadContextData = this.threadContext.getResults();

        return CollectionUtil.unmodifiable(threadContextData.keySet());
    }

    public <T extends AObject> T getCache(UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new ConditionParametersException();
        }

        CoreObjectRepositoryObject coreObjectRepository = this.factoryManager.getCoreObjectRepository();

        return coreObjectRepository.getByHandle(SpaceType.USER, handle);
    }

    public void deleteCache(UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new ConditionParametersException();
        }

        CoreObjectRepositoryObject coreObjectRepository = this.factoryManager.getCoreObjectRepository();

        coreObjectRepository.deleteByHandle(SpaceType.USER, handle);
    }

    public <T> T getParameter(Class<T> clazz, String name) {
        return this.getParameterOrDefaultProvider(clazz, name, () -> {
            throw new StatusNotExistedException();
        });
    }

    public <T> T getParameterOrDefault(Class<T> clazz, String name, T defaultValue) {
        return this.getParameterOrDefaultProvider(clazz, name, () -> defaultValue);
    }

    @SuppressWarnings("unchecked")
    public <T> T getParameterOrDefaultProvider(Class<T> clazz, String name, Provider<T> defaultValue) {
        if (ObjectUtil.isAnyNull(clazz, defaultValue) || StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        Map<String, Object> threadContextParameters = this.threadContext.getParameters();
        Object value = threadContextParameters.getOrDefault(name, null);

        if (ObjectUtil.isAnyNull(value)) {
            return defaultValue.acquire();
        } else {
            if (value.getClass() != clazz) {
                throw new StatusRelationshipErrorException();
            }

            return (T) value;
        }
    }

    public <T extends APrototype> void setParameter(Class<T> clazz, String name, Object value) {
        if (ObjectUtil.isAnyNull(clazz) || StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        Map<String, Object> threadContextParameters = this.threadContext.getParameters();
        threadContextParameters.put(name, value);
    }

    public void setResult(String name, Object value) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        Map<String, Object> threadContextResults = new HashMap<>(this.threadContext.getResults());
        threadContextResults.put(name, value);
        this.threadContext.setResults(threadContextResults);
    }

    public boolean isException() {
        return ObjectUtil.allNotNull(this.threadContext.getRunException());
    }

    public AKernelException getException() {
        return this.threadContext.getRunException();
    }

    public void setException(AKernelException exception) {
        this.threadContext.setRunException(exception);

    }
}
