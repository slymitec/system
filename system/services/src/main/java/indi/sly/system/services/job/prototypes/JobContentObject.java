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
    protected JobPointerObject pointer;

    public Set<String> getParameterNames() {
        Map<String, Object> threadContextData = this.threadContext.getParameters();

        return CollectionUtil.unmodifiable(threadContextData.keySet());
    }

    public Set<String> getResultNames() {
        Map<String, Object> threadContextData = this.threadContext.getResults();

        return CollectionUtil.unmodifiable(threadContextData.keySet());
    }

    public <T extends AObject> T getCache(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        CoreObjectRepositoryObject coreObjectRepository = this.factoryManager.getCoreObjectRepository();

        Class<? extends APrototype> prototypeType = this.pointer.getProtoTypes().getOrDefault(id, null);

        if (ObjectUtil.isAnyNull(prototypeType)) {
            throw new StatusNotExistedException();
        }

        return coreObjectRepository.getByHandle(SpaceType.USER, id);
    }

    public UUID setCache(String name, AObject value) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        CoreObjectRepositoryObject coreObjectRepository = this.factoryManager.getCoreObjectRepository();

        UUID handle = UUIDUtil.createRandom();

        this.pointer.getProtoTypes().put(handle, value.getClass());
        coreObjectRepository.addByHandle(SpaceType.USER, handle, value);

        return handle;
    }

    public void deleteCache(UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new ConditionParametersException();
        }

        CoreObjectRepositoryObject coreObjectRepository = this.factoryManager.getCoreObjectRepository();

        Class<? extends APrototype> prototypeType = this.pointer.getProtoTypes().getOrDefault(handle, null);

        if (ObjectUtil.isAnyNull(prototypeType)) {
            throw new StatusNotExistedException();
        }

        this.pointer.getProtoTypes().remove(handle);
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

    @SuppressWarnings("unchecked")
    public <T> T getOrTakeResult(Class<T> clazz, String name) {
        if (ObjectUtil.isAnyNull(clazz) || StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        Map<String, Object> threadContextResults = this.threadContext.getResults();
        Object value = threadContextResults.getOrDefault(name, null);
        T result;

        if (ObjectUtil.isAnyNull(value)) {
            return null;
        } else if (value instanceof APrototype && clazz == UUID.class) {
            CoreObjectRepositoryObject coreObjectRepository = this.factoryManager.getCoreObjectRepository();

            UUID handle = UUIDUtil.createRandom();
            AObject prototype = (AObject) value;

            this.pointer.getProtoTypes().put(handle, prototype.getClass());
            coreObjectRepository.addByHandle(SpaceType.USER, handle, prototype);

            result = (T) handle;
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
