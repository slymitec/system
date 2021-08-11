package indi.sly.system.services.job.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
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

    @SuppressWarnings("unchecked")
    public <T> T takeResult(Class<T> clazz, String name) {
        if (ObjectUtil.isAnyNull(clazz) || StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        Map<String, Object> threadContextData = this.threadContext.getResults();
        Object value = threadContextData.getOrDefault(name, null);

        if (ObjectUtil.isAnyNull(value)) {
            throw new StatusNotExistedException();
        } else if (value instanceof APrototype && clazz == UUID.class) {
            CorePrototypeRepositoryObject corePrototypeRepository = this.factoryManager.getCorePrototypeRepository();

            UUID id = UUIDUtil.createRandom();

            corePrototypeRepository.addByID(SpaceType.USER, id, (APrototype) value);

            return (T) id;
        } else if (value.getClass() != clazz) {
            throw new StatusRelationshipErrorException();
        } else {
            return (T) value;
        }
    }

    //

    public <T> T getDatum(Class<T> clazz, String name) {
        return this.getDatumOrDefaultProvider(clazz, name, () -> {
            throw new StatusNotExistedException();
        });
    }

    public <T> T getDatumOrDefault(Class<T> clazz, String name, T defaultValue) {
        return this.getDatumOrDefaultProvider(clazz, name, () -> defaultValue);
    }

    @SuppressWarnings("unchecked")
    public <T> T getDatumOrDefaultProvider(Class<T> clazz, String name, Provider<T> defaultValue) {
        if (ObjectUtil.isAnyNull(clazz, defaultValue) || StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        Map<String, Object> threadContextData = this.threadContext.getResults();
        Object value = threadContextData.getOrDefault(name, null);

        if (ObjectUtil.isAnyNull(value)) {
            return defaultValue.acquire();
        } else {
            if (value.getClass() != clazz) {
                throw new StatusRelationshipErrorException();
            }

            return (T) value;
        }
    }

    public void setDatum(String name, Object value) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        Map<String, Object> threadContextData = new HashMap<>(this.threadContext.getResults());
        threadContextData.put(name, value);
        this.threadContext.setResults(threadContextData);
    }

    public void deleteDatumIfExisted(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        if (this.threadContext.getResults().containsKey(name)) {
            Map<String, Object> threadContextData = new HashMap<>(this.threadContext.getResults());
            threadContextData.remove(name);
            this.threadContext.setResults(threadContextData);
        }
    }

    public Set<String> getNames() {
        Map<String, Object> threadContextData = this.threadContext.getResults();

        return CollectionUtil.unmodifiable(threadContextData.keySet());
    }

    public void clear() {
        this.threadContext.setResults(new HashMap<>());
    }

//    public UUID transferPrototypeToCache(String name) {
//        if (StringUtil.isNameIllegal(name)) {
//            throw new ConditionParametersException();
//        }
//
//        Map<String, Object> threadContextData = this.threadContext.getData();
//        Object value = threadContextData.getOrDefault(name, null);
//
//        if (ObjectUtil.isAnyNull(value)) {
//            throw new StatusNotExistedException();
//        }
//        if (!(value instanceof APrototype)) {
//            throw new StatusRelationshipErrorException();
//        }
//
//        threadContextData.remove(name);
//
//        CorePrototypeRepositoryObject corePrototypeRepository = this.factoryManager.getCorePrototypeRepository();
//        UUID id = UUIDUtil.createRandom();
//        APrototype prototype = (APrototype) value;
//        corePrototypeRepository.addByID(SpaceType.USER, id, prototype);
//
//        return id;
//    }

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
