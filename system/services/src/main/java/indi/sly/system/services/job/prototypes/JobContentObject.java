package indi.sly.system.services.job.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.prototypes.ThreadContextObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobContentObject extends APrototype {
    protected ThreadContextObject threadContext;

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

        Map<String, Object> threadContextData = this.threadContext.getData();
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

        Map<String, Object> threadContextData = new HashMap<>(this.threadContext.getData());
        threadContextData.put(name, value);
        this.threadContext.setData(threadContextData);
    }

    public void deleteDatumIfExisted(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        if (this.threadContext.getData().containsKey(name)) {
            Map<String, Object> threadContextData = new HashMap<>(this.threadContext.getData());
            threadContextData.remove(name);
            this.threadContext.setData(threadContextData);
        }
    }

    public Set<String> getNames() {
        Map<String, Object> threadContextData = this.threadContext.getData();

        return CollectionUtil.unmodifiable(threadContextData.keySet());
    }

    public void clear() {
        this.threadContext.setData(new HashMap<>());
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
