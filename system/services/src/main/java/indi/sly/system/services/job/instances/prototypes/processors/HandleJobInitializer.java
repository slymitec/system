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
import indi.sly.system.services.job.lang.JobRunConsumer;
import indi.sly.system.services.job.prototypes.JobContentObject;
import indi.sly.system.services.job.prototypes.processors.AJobInitializer;
import indi.sly.system.services.job.values.JobDefinition;
import indi.sly.system.services.job.values.JobTransactionType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HandleJobInitializer extends AJobInitializer {
    public HandleJobInitializer() {
        this.register("getAllHandle", this::getAllHandle, JobTransactionType.INDEPENDENCE);
        this.register("deleteHandle", this::deleteHandle, JobTransactionType.INDEPENDENCE);
        this.register("deleteAllHandle", this::deleteAllHandle, JobTransactionType.INDEPENDENCE);
        this.register("customHandle", this::customHandle, JobTransactionType.INDEPENDENCE);
    }

    @Override
    public void start(JobDefinition job) {
    }

    @Override
    public void finish(JobDefinition job) {
    }

    private void customHandle(JobRunConsumer run, JobContentObject content) {
        UUID parameter_HandleID = content.getParameterOrDefault(UUID.class, "handleID", null);
        String parameter_MethodName = content.getParameterOrDefault(String.class, "methodName", null);
        if (ValueUtil.isAnyNullOrEmpty(parameter_HandleID) || StringUtil.isNameIllegal(parameter_MethodName)) {
            throw new ConditionParametersException();
        }
        Class<?>[] parameter_MethodParameters = content.getParameterOrDefault(Class[].class, "methodParameterTypes", null);

        AObject object = content.getCache(parameter_HandleID);

        Class<? extends AObject> clazz = object.getClass();
        Method method;
        try {
            if (ObjectUtil.notNull(parameter_MethodParameters)) {
                method = clazz.getMethod(parameter_MethodName, parameter_MethodParameters);
            } else {
                method = clazz.getMethod(parameter_MethodName);
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

    private void getAllHandle(JobRunConsumer run, JobContentObject content) {
        Set<UUID> handles = content.getAllHandle();

        content.setResult("result", handles);
    }

    private void deleteHandle(JobRunConsumer run, JobContentObject content) {
        UUID parameter_handleID = content.getParameterOrDefault(UUID.class, "handleID", null);
        if (ValueUtil.isAnyNullOrEmpty(parameter_handleID)) {
            throw new ConditionParametersException();
        }

        content.deleteCache(parameter_handleID);
    }

    private void deleteAllHandle(JobRunConsumer run, JobContentObject content) {
        Set<UUID> handles = content.getAllHandle();

        for (UUID handle : handles) {
            content.deleteCache(handle);
        }
    }
}
