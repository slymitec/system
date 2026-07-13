package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.calls.values.ClientResponseRecord;
import indi.sly.subsystem.periphery.calls.values.ClientResponseExceptionRecord;
import indi.sly.subsystem.periphery.calls.values.ClientResponseExceptionTraceRecord;
import indi.sly.subsystem.periphery.calls.values.UserContentResponseRecord;
import indi.sly.subsystem.periphery.core.prototypes.ACacheableObject;
import indi.sly.subsystem.periphery.proxies.ProxyManager;
import indi.sly.subsystem.periphery.proxies.lang.ProxyInvokeFunction;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.ProxyProcessorMediator;
import indi.sly.subsystem.periphery.proxies.values.HandleContextDefinition;
import indi.sly.subsystem.periphery.proxies.values.ProxyCacheEntity;
import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.UUID;

public abstract class AProxy extends ACacheableObject<ProxyCacheEntity> {
    protected ProxyFactory factory;
    protected ProxyProcessorMediator processorMediator;

    public UUID getTargetHandle() {
        return this.cache.getHandle();
    }

    @SuppressWarnings("unchecked")
    protected <T> T invoke(String method, Class<T> returnClazz, Object... arg) {
        if (ValueUtil.isAnyNullOrEmpty(method) || ObjectUtil.isAnyNull(returnClazz)) {
            throw new ConditionParametersException();
        }

        ClientResponseRecord clientResponse = null;

        Set<ProxyInvokeFunction> invokes = this.processorMediator.getInvokes();

        for (ProxyInvokeFunction invoke : invokes) {
            clientResponse = invoke.apply(clientResponse, this.cache, method, arg);
        }

        if (ObjectUtil.isAnyNull(clientResponse)) {
            throw new StatusUnexpectedException();
        }

        UserContentResponseRecord clientResponseContent = clientResponse.getContent();
        ClientResponseExceptionRecord clientResponseException = clientResponse.getException();
        if (ObjectUtil.allNotNull(clientResponseException)) {
            ASystemException causeSystemException;
            try {
                Class<?> causeSystemExceptionClass = Class.forName("indi.sly.system.common.lang" + clientResponseException.clazz());

                causeSystemException = (ASystemException) causeSystemExceptionClass.getDeclaredConstructor().newInstance();

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                     InvocationTargetException | NoSuchMethodException _) {
                causeSystemException = new StatusUnexpectedException();
            }

            StackTraceElement[] stackTraceElements = new StackTraceElement[clientResponseException.trace().size()];
            for (int i = 0; i < clientResponseException.trace().size(); i++) {
                ClientResponseExceptionTraceRecord clientResponseExceptionTrace = clientResponseException.trace().get(i);
                stackTraceElements[i] = new StackTraceElement(clientResponseExceptionTrace.clazz(), clientResponseExceptionTrace.method(), StringUtil.EMPTY, 1);
            }

            causeSystemException.setStackTrace(stackTraceElements);

            throw new SystemException(causeSystemException);
        } else if (ObjectUtil.allNotNull(clientResponseContent)) {
            if (ClassUtil.isThisOrSuperContain(returnClazz, ACacheableObject.class)) {
                if (ClassUtil.getSimpleName(HandleContextDefinition.class).equals(clientResponseContent.clazz())) {
                    throw new StatusRelationshipErrorException();
                }

                HandleContextDefinition handleContext = ObjectUtil.transferFromString(HandleContextDefinition.class, clientResponseContent.value());

                if (ClassUtil.getSimpleName(HandleContextDefinition.class).equals(clientResponseContent.clazz())) {
                    throw new StatusRelationshipErrorException();
                }

                if (ClassUtil.getSimpleName(HandleContextDefinition.class).equals(handleContext.getClazz())) {
                    throw new StatusRelationshipErrorException();
                }

                ProxyManager proxyManager = this.coreManager.getManager(ProxyManager.class);

                return (T) proxyManager.getFactory().buildProxy(handleContext.getClazz());

            } else {
                if (!ClassUtil.getSimpleName(returnClazz).equals(clientResponseContent.clazz())) {
                    throw new StatusRelationshipErrorException();
                }

                if (returnClazz != Void.class) {
                    return ObjectUtil.transferFromString(returnClazz, clientResponseContent.value());
                } else {
                    return null;
                }
            }
        } else {
            throw new StatusUnexpectedException();
        }
    }
}
