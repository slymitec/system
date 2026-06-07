package indi.sly.subsystem.periphery.proxies.prototypes.processors;

import indi.sly.subsystem.periphery.calls.CallManager;
import indi.sly.subsystem.periphery.calls.prototypes.ConnectionObject;
import indi.sly.subsystem.periphery.calls.values.*;
import indi.sly.subsystem.periphery.core.prototypes.ACacheableObject;
import indi.sly.subsystem.periphery.core.prototypes.processors.AResolver;
import indi.sly.subsystem.periphery.proxies.ProxyManager;
import indi.sly.subsystem.periphery.proxies.lang.ProxyInvokeFunction;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.ProxyProcessorMediator;
import indi.sly.subsystem.periphery.proxies.values.HandleContextDefinition;
import indi.sly.subsystem.periphery.proxies.values.ProxyCacheEntity;
import indi.sly.system.common.lang.ASystemException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.lang.SystemException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.lang.reflect.InvocationTargetException;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProxyCallResolver extends AResolver implements IProxyResolver {
    private final ProxyInvokeFunction invoke;

    public ProxyCallResolver() {
        this.invoke = (result, proxy, method, responseClazz, parameters) -> {
            CallManager callManager = this.coreManager.getManager(CallManager.class);

            ClientRequestDefinition clientRequest = new ClientRequestDefinition();
            ClientRequestProcessIdDefinition clientRequestProcessId = clientRequest.getProcessId();
            clientRequestProcessId.setId(proxy.getContext().getProcessId());
            clientRequestProcessId.setType(proxy.getContext().getType());
            clientRequestProcessId.setSecret(proxy.getContext().getSecret());
            clientRequestProcessId.setVerification(proxy.getContext().getVerification());
            UserContentRequestDefinition clientRequestContent = clientRequest.getContent();
            clientRequestContent.setId(UUIDUtil.createRandom());
            clientRequestContent.setTask(proxy.getTask());
            clientRequestContent.setMethod(method);
            if (ObjectUtil.allNotNull(parameters)) {
                for (Object parameter : parameters) {
                    clientRequestContent.getParameters().add(ObjectUtil.transferToString(parameter));
                }
            }

            ConnectionObject connection = callManager.getConnection(proxy.getContext().getCall());

            ClientResponseDefinition clientResponse = connection.call(clientRequest);

            UserContentResponseDefinition clientResponseContent = clientResponse.getContent();
            ClientResponseExceptionDefinition clientResponseException = clientResponse.getException();
            if (ObjectUtil.allNotNull(clientResponseException)) {
                if (!clientResponseException.getId().equals(clientRequestContent.getId())) {
                    throw new StatusRelationshipErrorException();
                }

                ASystemException causeSystemException;
                try {
                    Class<?> causeSystemExceptionClass = Class.forName("indi.sly.system.common.lang" + clientResponseException.getClazz());

                    causeSystemException = (ASystemException) causeSystemExceptionClass.getDeclaredConstructor().newInstance();

                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                         InvocationTargetException | NoSuchMethodException _) {
                    causeSystemException = new StatusUnexpectedException();
                }

                StackTraceElement[] stackTraceElements = new StackTraceElement[clientResponseException.getTrace().size()];
                for (int i = 0; i < clientResponseException.getTrace().size(); i++) {
                    ClientResponseExceptionTraceDefinition clientResponseExceptionTrace = clientResponseException.getTrace().get(i);
                    stackTraceElements[i] = new StackTraceElement(clientResponseExceptionTrace.getClazz(), clientResponseExceptionTrace.getMethod(), StringUtil.EMPTY, 1);
                }

                causeSystemException.setStackTrace(stackTraceElements);

                throw new SystemException(causeSystemException);
            } else {
                if (!clientResponseContent.getId().equals(clientRequestContent.getId())) {
                    throw new StatusRelationshipErrorException();
                }

                if (ClassUtil.isThisOrSuperContain(responseClazz, ACacheableObject.class)) {
                    if (ClassUtil.getSimpleName(HandleContextDefinition.class).equals(clientResponseContent.getClazz())) {
                        throw new StatusRelationshipErrorException();
                    }

                    HandleContextDefinition handleContext = ObjectUtil.transferFromString(HandleContextDefinition.class, clientResponseContent.getValue());

                    if (ClassUtil.getSimpleName(HandleContextDefinition.class).equals(clientResponseContent.getClazz())) {
                        throw new StatusRelationshipErrorException();
                    }

                    if (ClassUtil.getSimpleName(HandleContextDefinition.class).equals(handleContext.getClazz())) {
                        throw new StatusRelationshipErrorException();
                    }

                    ProxyManager proxyManager = this.coreManager.getManager(ProxyManager.class);

                    return proxyManager.getFactory().buildProxy(handleContext.getClazz());

                } else {
                    if (!ClassUtil.getSimpleName(responseClazz).equals(clientResponseContent.getClazz())) {
                        throw new StatusRelationshipErrorException();
                    }

                    if (responseClazz != Void.class) {
                        return ObjectUtil.transferFromString(responseClazz, clientResponseContent.getValue());
                    } else {
                        return null;
                    }
                }
            }
        };
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public void resolve(ProxyCacheEntity proxy, ProxyProcessorMediator processorMediator) {
        processorMediator.getInvokes().add(invoke);
    }
}
