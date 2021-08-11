package indi.sly.system.services.job.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.*;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.core.prototypes.CorePrototypeRepositoryObject;
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
public class JobContentObject extends APrototype {
    protected ThreadContextObject threadContext;
    protected JobPointerObject pointer;

    public void setResult(String name, Object value) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        Map<String, Object> threadContextResults = new HashMap<>(this.threadContext.getResults());
        threadContextResults.put(name, value);
        this.threadContext.setResults(threadContextResults);
    }

    @SuppressWarnings("unchecked")
    public <T> T takeResult(Class<T> clazz, String name) {
        if (ObjectUtil.isAnyNull(clazz) || StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        Map<String, Object> threadContextResults = this.threadContext.getResults();
        Object value = threadContextResults.getOrDefault(name, null);
        T result;

        if (ObjectUtil.isAnyNull(value)) {
            return null;
        } else if (value instanceof APrototype && clazz == UUID.class) {
            CorePrototypeRepositoryObject corePrototypeRepository = this.factoryManager.getCorePrototypeRepository();

            UUID id = UUIDUtil.createRandom();
            APrototype prototype = (APrototype) value;

            this.pointer.getProtoTypes().put(id, prototype.getClass());
            corePrototypeRepository.addByID(SpaceType.USER, id, prototype);

            result = (T) id;
        } else if (value.getClass() != clazz) {
            throw new StatusRelationshipErrorException();
        } else {
            result = (T) value;
        }

        threadContextResults = new HashMap<>(threadContextResults);
        threadContextResults.remove(name);
        this.threadContext.setResults(threadContextResults);

        return result;
    }

    public <T extends APrototype> void injectParameter(UUID id, String name) {
        if (ValueUtil.isAnyNullOrEmpty(id) || StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        Class<? extends APrototype> prototypeType = this.pointer.getProtoTypes().getOrDefault(id, null);

        if (ObjectUtil.isAnyNull(prototypeType)) {
            throw new StatusNotExistedException();
        }

        CorePrototypeRepositoryObject corePrototypeRepository = this.factoryManager.getCorePrototypeRepository();
        APrototype prototype = corePrototypeRepository.getByID(SpaceType.USER, prototypeType, id);

        Map<String, Object> threadContextParameters = this.threadContext.getParameters();
        threadContextParameters.put(name, prototype);
    }

    public <T extends APrototype> void setParameter(Class<T> clazz, String name, Object value) {
        if (ObjectUtil.isAnyNull(clazz) || StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        Map<String, Object> threadContextParameters = this.threadContext.getParameters();
        threadContextParameters.put(name, value);
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

    //

    public Set<String> getParameterNames() {
        Map<String, Object> threadContextData = this.threadContext.getParameters();

        return CollectionUtil.unmodifiable(threadContextData.keySet());
    }

    public Set<String> getResultNames() {
        Map<String, Object> threadContextData = this.threadContext.getResults();

        return CollectionUtil.unmodifiable(threadContextData.keySet());
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
