package indi.sly.system.services.jobs.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.kernel.core.prototypes.ObjectCollectionObject;
import indi.sly.system.kernel.core.values.ACacheEntity;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ACacheRepositoryObject;
import indi.sly.system.kernel.processes.prototypes.ThreadContextObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskContentObject extends AObject {
    protected ThreadContextObject threadContext;

    @SuppressWarnings("unchecked")
    public <T extends ACacheEntity> T getCache(UUID cacheRepositoryId, UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(cacheRepositoryId, handle)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ACacheRepositoryObject<?> cacheRepository = memoryManager.getCacheRepository(cacheRepositoryId);

        return (T) cacheRepository.get(handle);
    }

    public void deleteCache(UUID cacheRepositoryId, UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(cacheRepositoryId, handle)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ACacheRepositoryObject<?> cacheRepository = memoryManager.getCacheRepository(cacheRepositoryId);

        cacheRepository.delete(handle);
    }

    public void refreshCache(UUID cacheRepositoryId, UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(cacheRepositoryId, handle)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ACacheRepositoryObject<?> cacheRepository = memoryManager.getCacheRepository(cacheRepositoryId);

        cacheRepository.refresh(handle);
    }

    public List<String> getParameters() {
        return this.threadContext.getParameters();
    }

    public void setParameter(List<String> parameters) {
        if (ValueUtil.isAnyNullOrEmpty(parameters)) {
            throw new ConditionParametersException();
        }
        
        this.threadContext.setParameters(parameters);
    }

    public Object getResult() {
        return this.threadContext.getResult();
    }

    public void setResult(Object value) {
        this.threadContext.setResult(value);
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
