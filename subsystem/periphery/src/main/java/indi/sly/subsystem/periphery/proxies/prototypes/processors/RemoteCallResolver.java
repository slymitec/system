package indi.sly.subsystem.periphery.proxies.prototypes.processors;

import indi.sly.subsystem.periphery.calls.CallManager;
import indi.sly.subsystem.periphery.calls.prototypes.ConnectionObject;
import indi.sly.subsystem.periphery.calls.values.*;
import indi.sly.subsystem.periphery.core.prototypes.processors.AResolver;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorExpireConsumer;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorInvokeFunction;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.RemoteProcessorMediator;
import indi.sly.subsystem.periphery.proxies.values.*;
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
import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RemoteCallResolver extends AResolver implements IRemoteResolver {
    private final RemoteProcessorInvokeFunction invoke;
    private final RemoteProcessorExpireConsumer expire;

    public RemoteCallResolver() {
        this.invoke = (invokeRemote, remote, procedure, method, parameters) -> {
            CallManager callManager = this.coreManager.getManager(CallManager.class);

            ProcedureProcessRecord proxyContextProcess = procedure.getProcess();
            ClientRequestProcessIdRecord clientRequestProcessId = new ClientRequestProcessIdRecord(proxyContextProcess.id(), proxyContextProcess.type(), proxyContextProcess.secret(), proxyContextProcess.verification());

            List<String> clientRequestContentParameters = new ArrayList<>();
            if (ObjectUtil.allNotNull(parameters)) {
                for (Object parameter : parameters) {
                    clientRequestContentParameters.add(ObjectUtil.transferToString(parameter));
                }
            }

            ConnectionObject connection = callManager.getConnection(procedure.getCall());

            UserContentRequestRecord clientRequestContent = new UserContentRequestRecord(UUIDUtil.createRandom(), remote.getClazz(), method, clientRequestContentParameters);
            ClientRequestRecord clientRequest = new ClientRequestRecord(clientRequestProcessId, clientRequestContent);

            ClientResponseRecord clientResponse = connection.call(clientRequest);

            UserContentResponseRecord clientResponseContent = clientResponse.content();
            ClientResponseExceptionRecord clientResponseException = clientResponse.exception();

            if (ObjectUtil.allNotNull(clientResponseException)) {
                if (!clientResponseException.id().equals(clientRequestContent.id())) {
                    throw new StatusRelationshipErrorException();
                }

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
                if (!clientResponseContent.id().equals(clientRequestContent.id())) {
                    throw new StatusRelationshipErrorException();
                }

                invokeRemote = new RemoteDefinition();

                if (ClassUtil.getSimpleName(HandleContextRecord.class).equals(clientResponseContent.clazz())) {
                    HandleContextRecord handleContext = ObjectUtil.transferFromString(HandleContextRecord.class, clientResponseContent.value());

                    invokeRemote.setType(RemoteTypes.OBJECT);
                    invokeRemote.setClazz(handleContext.clazz());
                    invokeRemote.setValue(ObjectUtil.transferToString(handleContext.handle()));
                } else {
                    invokeRemote.setType(RemoteTypes.VALUE);
                    invokeRemote.setClazz(clientResponseContent.clazz());
                    invokeRemote.setValue(clientResponseContent.value());
                }

                return invokeRemote;
            } else {
                throw new StatusUnexpectedException();
            }
        };

        this.expire = (remote, procedure, duration) -> {
            this.invoke.apply(null, remote, procedure, "expire", new Object[]{duration});
        };
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public void resolve(RemoteDefinition remote, RemoteProcessorMediator processorMediator) {
        processorMediator.getInvokes().add(invoke);
        processorMediator.getExpires().add(expire);
    }
}
