package indi.sly.system.services.job.prototypes;

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

import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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

    public <T> T getParameter(Class<T> clazz, String name) {
        return ObjectUtil.transferFromStringOrDefaultProvider(clazz, this.getParameter(name), () -> {
            throw new StatusUnreadableException();
        });
    }

    public <T> T getParameterOrDefault(Class<T> clazz, String name, T defaultValue) {
        String parameter;
        try {
            parameter = this.getParameter(name);
        } catch (StatusNotExistedException ignore) {
            return defaultValue;
        }

        return ObjectUtil.transferFromStringOrDefault(clazz, parameter, defaultValue);
    }

    public String getParameter(String name) {
        return this.getParameterOrDefaultProvider(name, () -> {
            throw new StatusNotExistedException();
        });
    }

    public String getParameterOrDefault(String name, String defaultValue) {
        return this.getParameterOrDefaultProvider(name, () -> defaultValue);
    }

    public String getParameterOrDefaultProvider(String name, Provider<String> defaultValue) {
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

    public boolean isException() {
        return ObjectUtil.allNotNull(this.threadContext.getRunException());
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

    public void clearException() {
        this.threadContext.setRunException(null);
    }
}
