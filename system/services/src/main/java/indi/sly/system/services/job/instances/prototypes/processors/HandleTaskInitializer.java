package indi.sly.system.services.job.instances.prototypes.processors;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.services.job.lang.TaskRunConsumer;
import indi.sly.system.services.job.prototypes.TaskContentObject;
import indi.sly.system.services.job.values.TaskDefinition;
import indi.sly.system.services.core.values.TransactionType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HandleTaskInitializer extends ATaskInitializer {
    public HandleTaskInitializer() {
        this.register("getAllHandle", this::getAllHandle, TransactionType.INDEPENDENCE);
        this.register("deleteHandle", this::deleteHandle, TransactionType.INDEPENDENCE);
        this.register("deleteAllHandle", this::deleteAllHandle, TransactionType.INDEPENDENCE);
        this.register("customHandle", this::customHandle, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void customHandle(TaskRunConsumer run, TaskContentObject content) {
        UUID handleID = content.getParameterOrDefault(UUID.class, "handleID", null);
        String methodName = content.getParameterOrDefault(String.class, "method", null);
        if (ValueUtil.isAnyNullOrEmpty(handleID) || StringUtil.isNameIllegal(methodName)) {
            throw new ConditionParametersException();
        }
        Class<?>[] methodParameters = content.getParameterOrDefault(Class[].class, "methodParameterTypes", null);

        AObject object = content.getCache(handleID);

        Class<? extends AObject> clazz = object.getClass();
        Method method;
        try {
            if (ObjectUtil.notNull(methodParameters)) {
                method = clazz.getMethod(methodName, methodParameters);
            } else {
                method = clazz.getMethod(methodName);
            }
        } catch (NoSuchMethodException e) {
            throw new StatusNotExistedException();
        }
        Class<?>[] methodParameterTypes = method.getParameterTypes();
        Object[] methodParameterValues = new Object[methodParameterTypes.length];
        Class<?> methodReturnType = method.getReturnType();

        for (int i = 0; i < methodParameterTypes.length; i++) {
            if (ClassUtil.isThisOrSuperContain(methodParameterTypes[i], AObject.class)) {
                UUID handle = content.getParameterOrDefault(UUID.class, "methodParameter" + i, null);
                methodParameterValues[i] = content.getCache(handle);
            } else {
                methodParameterValues[i] = content.getParameterOrDefault(Object.class, "methodParameter" + i, null);
            }
        }

        Object result;
        try {
            result = method.invoke(object, methodParameterValues);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new StatusUnexpectedException();
        }
        if (methodReturnType != void.class) {
            if (ClassUtil.isThisOrSuperContain(methodReturnType, AObject.class)) {
                AObject resultObject = (AObject) result;
                UUID handle = resultObject.cache(SpaceType.USER);
                content.setResult("result", handle);
            } else {
                content.setResult("result", result);
            }
        }
    }

    private void getAllHandle(TaskRunConsumer run, TaskContentObject content) {
        Set<UUID> handles = content.getAllHandle();

        content.setResult("result", handles);
    }

    private void deleteHandle(TaskRunConsumer run, TaskContentObject content) {
        UUID handleID = content.getParameterOrDefault(UUID.class, "handleID", null);
        if (ValueUtil.isAnyNullOrEmpty(handleID)) {
            throw new ConditionParametersException();
        }

        content.deleteCache(handleID);
    }

    private void deleteAllHandle(TaskRunConsumer run, TaskContentObject content) {
        Set<UUID> handles = content.getAllHandle();

        for (UUID handle : handles) {
            content.deleteCache(handle);
        }
    }
}
