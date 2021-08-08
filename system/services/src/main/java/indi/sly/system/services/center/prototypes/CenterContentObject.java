package indi.sly.system.services.center.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.prototypes.ThreadContextObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CenterContentObject extends APrototype {
    protected ThreadContextObject threadContext;

    public <T> T getDatum(Class<T> clazz, String name) {
        return this.getDatumOrDefault(clazz, name, (Provider<T>) () -> {
            throw new StatusNotExistedException();
        });
    }

    public <T> T getDatumOrDefault(Class<T> clazz, String name, T defaultValue) {
        return this.getDatumOrDefault(clazz, name, (Provider<T>) () -> defaultValue);
    }

    @SuppressWarnings("unchecked")
    public <T> T getDatumOrDefault(Class<T> clazz, String name, Provider<T> defaultValue) {
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

        Map<String, Object> threadContextData = this.threadContext.getData();

        threadContextData.put(name, value);
    }

    public Map<String, String> getData() {
        Map<String, String> data = new HashMap<>();

        for (Map.Entry<String, Object> datum : this.threadContext.getData().entrySet()) {
            data.put(datum.getKey(), ObjectUtil.transferToString(datum.getValue()));
        }

        return data;
    }

    public void setData(Map<String, String> data, Map<String, Class<?>> classes) {
        if (ObjectUtil.isAnyNull(data, classes) || data.size() != classes.size()) {
            throw new ConditionParametersException();
        }

        Map<String, Object> threadContextData = new HashMap<>();

        for (Map.Entry<String, String> datum : data.entrySet()) {
            if (StringUtil.isNameIllegal(datum.getKey())) {
                throw new ConditionParametersException();
            } else {
                Class<?> clazz = classes.getOrDefault(datum.getKey(), null);
                if (ObjectUtil.isAnyNull(clazz)) {
                    throw new ConditionParametersException();
                }

                threadContextData.put(datum.getKey(), ObjectUtil.transferFromString(clazz, datum.getValue()));
            }
        }

        this.threadContext.getData().putAll(threadContextData);
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
