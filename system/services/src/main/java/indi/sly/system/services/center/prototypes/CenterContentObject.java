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

    @SuppressWarnings("unchecked")
    public <T> T getDatum(Class<T> clazz, String name) {
        Map<String, Object> threadContextData = this.threadContext.getData();

        Object value = threadContextData.getOrDefault(name, null);

        if (ObjectUtil.isAnyNull(value)) {
            return null;
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

        for (Map.Entry<String, Object> pair : this.threadContext.getData().entrySet()) {
            data.put(pair.getKey(), ObjectUtil.transferToString(pair.getValue()));
        }

        return data;
    }

    public void setData(Map<String, String> data, Map<String, Class<?>> classes) {
        if (ObjectUtil.isAnyNull(data, classes) || data.size() != classes.size()) {
            throw new ConditionParametersException();
        }

        Map<String, Object> threadContextData = new HashMap<>();

        for (Map.Entry<String, String> pair : data.entrySet()) {
            if (StringUtil.isNameIllegal(pair.getKey())) {
                throw new ConditionParametersException();
            } else {
                Class<?> clazz = classes.getOrDefault(pair.getKey(), null);
                if (ObjectUtil.isAnyNull(clazz)) {
                    throw new ConditionParametersException();
                }

                threadContextData.put(pair.getKey(), ObjectUtil.transferFromString(clazz, pair.getValue()));
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
