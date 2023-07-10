package indi.sly.system.services.jobs.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.kernel.core.prototypes.CoreObjectRepositoryObject;
import indi.sly.system.kernel.processes.prototypes.ThreadContextObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskContentObject extends AObject {
    protected ThreadContextObject threadContext;

    public Set<UUID> getAllHandle() {
        CoreObjectRepositoryObject coreObjectRepository = this.factoryManager.getCoreObjectRepository();

        return coreObjectRepository.getAllHandle(SpaceType.USER);
    }

    public <T extends AObject> T getCache(UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new ConditionParametersException();
        }

        CoreObjectRepositoryObject coreObjectRepository = this.factoryManager.getCoreObjectRepository();

        return coreObjectRepository.getByHandle(SpaceType.USER, handle);
    }

    public <T extends AObject> T getCacheByParameterName(String name) {
        UUID handle = this.getParameter(UUID.class, name);
        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new StatusNotExistedException();
        }

        return this.getCache(handle);
    }

    public <T extends AObject> T getCacheByParameterNameOrDefault(String name, T defaultValue) {
        UUID handle = this.getParameter(UUID.class, name);
        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            return defaultValue;
        }

        return this.getCache(handle);
    }

    public void deleteCache(UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new ConditionParametersException();
        }

        AObject object = this.getCache(handle);
        object.uncache(SpaceType.USER);
    }

    public boolean isParameterExist(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        Map<String, String> threadContextParameters = this.threadContext.getParameters();

        return threadContextParameters.containsKey(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getParameter(Class<T> clazz, String name) {
        if (clazz == String.class) {
            return (T) this.getParameter(name);
        }

        return ObjectUtil.transferFromStringOrDefaultProvider(clazz, this.getParameter(name), () -> {
            throw new StatusUnreadableException();
        });
    }

    @SuppressWarnings("unchecked")
    public <T> T getParameterOrNull(Class<T> clazz, String name) {
        if (clazz == String.class) {
            return (T) this.getParameterOrNull(name);
        }

        if (!this.isParameterExist(name)) {
            return null;
        }

        return ObjectUtil.transferFromStringOrDefaultProvider(clazz, this.getParameter(name), () -> {
            throw new StatusUnreadableException();
        });
    }

    public String getParameter(String name) {
        return this.getParameterOrDefaultProvider(name, () -> {
            throw new StatusNotExistedException();
        });
    }

    public String getParameterOrNull(String name) {
        return this.getParameterOrDefaultProvider(name, () -> null);
    }

    public <T> List<T> getParameterList(Class<T> clazz, String name) {
        return ObjectUtil.transferListFromStringOrDefaultProvider(clazz, this.getParameter(name), () -> {
            throw new StatusUnreadableException();
        });
    }

    public <T> List<T> getParameterListOrNull(Class<T> clazz, String name) {
        if (!this.isParameterExist(name)) {
            return null;
        }

        return ObjectUtil.transferListFromStringOrDefaultProvider(clazz, this.getParameter(name), () -> {
            throw new StatusUnreadableException();
        });
    }

    public <TK, TV> Map<TK, TV> getParameterMap(Class<TK> keyClass, Class<TV> valueClass, String name) {
        return ObjectUtil.transferMapFromStringOrDefaultProvider(keyClass, valueClass, this.getParameter(name), () -> {
            throw new StatusUnreadableException();
        });
    }

    public <TK, TV> Map<TK, TV> getParameterMapOrNull(Class<TK> keyClass, Class<TV> valueClass, String name) {
        if (!this.isParameterExist(name)) {
            return null;
        }

        return ObjectUtil.transferMapFromStringOrDefaultProvider(keyClass, valueClass, this.getParameter(name), () -> {
            throw new StatusUnreadableException();
        });
    }

    private String getParameterOrDefaultProvider(String name, Provider<String> defaultValue) {
        if (ObjectUtil.isAnyNull(defaultValue) || StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        Map<String, String> threadContextParameters = this.threadContext.getParameters();

        if (threadContextParameters.containsKey(name)) {
            return threadContextParameters.get(name);
        } else {
            return defaultValue.acquire();
        }
    }

    public void setParameter(String name, String value) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        Map<String, String> threadContextParameters = new HashMap<>(this.threadContext.getParameters());
        threadContextParameters.put(name, value);
        this.threadContext.setParameters(threadContextParameters);
    }

    public boolean isResultExist(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        Map<String, Object> threadContextResults = new HashMap<>(this.threadContext.getResults());

        return threadContextResults.containsKey(name);
    }

    public Map<String, Object> getResult() {
        return this.threadContext.getResults();
    }

    public void setResult(String name, Object value) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        Map<String, Object> threadContextResults = new HashMap<>(this.threadContext.getResults());
        threadContextResults.put(name, value);
        this.threadContext.setResults(threadContextResults);
    }

    public AKernelException getException() {
        return this.threadContext.getRunException();
    }

    public void setException(AKernelException exception) {
        if (ObjectUtil.isAnyNull(exception)) {
            throw new ConditionParametersException();
        }

        this.threadContext.setRunException(exception);
    }
}
