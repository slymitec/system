package indi.sly.system.services.job.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.CollectionUtil;
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
public class JobContentObject extends AObject {
    protected ThreadContextObject threadContext;

    public Set<String> getParameterNames() {
        Map<String, String> threadContextData = this.threadContext.getParameters();

        return CollectionUtil.unmodifiable(threadContextData.keySet());
    }

    public Set<String> getResultNames() {
        Map<String, String> threadContextData = this.threadContext.getResults();

        return CollectionUtil.unmodifiable(threadContextData.keySet());
    }

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

    public <T> T getParameterOrDefaultProvider(Class<T> clazz, String name, Provider<T> defaultValue) {
        if (ObjectUtil.isAnyNull(clazz, defaultValue) || StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        Map<String, String> threadContextParameters = this.threadContext.getParameters();
        String value = threadContextParameters.getOrDefault(name, null);

        if (ObjectUtil.isAnyNull(value)) {
            return defaultValue.acquire();
        } else {
            if (value.getClass() != clazz) {
                throw new StatusRelationshipErrorException();
            }

            try {
                return ObjectUtil.transferFromString(clazz, value);
            } catch (RuntimeException ignored) {
                throw new StatusUnreadableException();
            }
        }
    }

    public <T extends AObject> T getCacheFromParameter(String name) {
        UUID handle = this.getParameterOrDefaultProvider(UUID.class, name, null);

        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            return null;
        }

        return this.getCache(handle);
    }

    public <T> void setParameter(Class<T> clazz, String name, Object value) {
        if (ObjectUtil.isAnyNull(clazz) || StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        Map<String, String> threadContextParameters = this.threadContext.getParameters();
        threadContextParameters.put(name, ObjectUtil.transferToString(value));
    }

    public void setResult(String name, Object value) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        Map<String, String> threadContextResults = new HashMap<>(this.threadContext.getResults());
        threadContextResults.put(name, ObjectUtil.transferToString(value));
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
