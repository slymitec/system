package indi.sly.system.services.jobs.instances.prototypes.processors;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.*;
import indi.sly.system.common.values.MethodScopeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.HandledObjectDefinition;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HandleActionTaskInitializer extends ATaskInitializer {
    public HandleActionTaskInitializer() {
        this.register("getAllHandles", this::getAllHandles, TransactionType.INDEPENDENCE);
        this.register("getHandleClass", this::getHandleClass, TransactionType.INDEPENDENCE);
        this.register("containHandle", this::containHandle, TransactionType.INDEPENDENCE);
        this.register("deleteHandle", this::deleteHandle, TransactionType.INDEPENDENCE);
        this.register("deleteAllHandles", this::deleteAllHandles, TransactionType.INDEPENDENCE);
        this.register("customHandle", this::customHandle, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void customHandle(TaskRunConsumer run, TaskContentObject content) {
        UUID handleID = content.getParameter(UUID.class, "handle");
        String methodName = content.getParameter("method");
        if (ValueUtil.isAnyNullOrEmpty(handleID) || StringUtil.isNameIllegal(methodName)) {
            throw new ConditionParametersException();
        }
        String[] methodParameters = content.getParameterOrNull(String[].class, "parameterTypes");
        Class<?>[] methodParameterTypes;
        if (ArrayUtil.isNullOrEmpty(methodParameters)) {
            methodParameterTypes = new Class[0];
        } else {
            methodParameterTypes = new Class[methodParameters.length];

            try {
                for (int i = 0; i < methodParameters.length; i++) {
                    methodParameterTypes[i] = Class.forName(methodParameters[i]);
                }
            } catch (ClassNotFoundException e) {
                throw new StatusUnreadableException();
            }
        }

        AObject object = content.getCache(handleID);

        Class<? extends AObject> clazz = object.getClass();
        Method method;
        try {
            method = clazz.getMethod(methodName, methodParameterTypes);
        } catch (NoSuchMethodException e) {
            throw new StatusNotExistedException();
        }

        MethodScope methodScope = AnnotationUtil.getAnnotationFormThisAndSuperOrNull(method, MethodScope.class);
        if (ObjectUtil.notNull(methodScope) && LogicalUtil.isAnyExist(methodScope.value(), MethodScopeType.ONLY_KERNEL)) {
            throw new StatusNotSupportedException();
        }

        methodParameterTypes = method.getParameterTypes();
        Object[] methodParameterValues = new Object[methodParameterTypes.length];
        Class<?> methodReturnType = method.getReturnType();

        for (int i = 0; i < methodParameterTypes.length; i++) {
            if (ClassUtil.isThisOrSuperContain(methodParameterTypes[i], AObject.class)) {
                HandledObjectDefinition handledObject = content.getParameter(HandledObjectDefinition.class, content.getParameter("parameterValue_" + i));

                if (handledObject.getType() != methodParameterTypes[i]) {
                    throw new ConditionParametersException();
                }

                methodParameterValues[i] = content.getCache(handledObject.getHandle());
            } else {
                methodParameterValues[i] = content.getParameter(methodParameterTypes[i], content.getParameter("parameterValue_" + i));
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

                HandledObjectDefinition handledObject = new HandledObjectDefinition();
                handledObject.setHandle(handle);
                handledObject.setType(resultObject.getClass());

                content.setResult(handledObject);
            } else {
                content.setResult(result);
            }
        }
    }

    private void getAllHandles(TaskRunConsumer run, TaskContentObject content) {
        Set<UUID> handles = content.getAllHandles();

        content.setResult(handles);
    }

    private void getHandleClass(TaskRunConsumer run, TaskContentObject content) {
        UUID handleID = content.getParameter(UUID.class, "handleID");
        if (ValueUtil.isAnyNullOrEmpty(handleID)) {
            throw new ConditionParametersException();
        }

        AObject object = content.getCache(handleID);

        content.setResult(object.getClass());
    }

    private void containHandle(TaskRunConsumer run, TaskContentObject content) {
        UUID handleID = content.getParameter(UUID.class, "handleID");
        if (ValueUtil.isAnyNullOrEmpty(handleID)) {
            throw new ConditionParametersException();
        }

        boolean result = content.getAllHandles().contains(handleID);

        content.setResult(result);
    }

    private void deleteHandle(TaskRunConsumer run, TaskContentObject content) {
        UUID handleID = content.getParameter(UUID.class, "handleID");
        if (ValueUtil.isAnyNullOrEmpty(handleID)) {
            throw new ConditionParametersException();
        }

        content.deleteCache(handleID);
    }

    private void deleteAllHandles(TaskRunConsumer run, TaskContentObject content) {
        Set<UUID> handles = content.getAllHandles();

        for (UUID handle : handles) {
            content.deleteCache(handle);
        }
    }
}
