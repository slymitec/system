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
        this.invoke = (clientResponse, proxy, method, parameters) -> {
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

            clientResponse = connection.call(clientRequest);

            UserContentResponseDefinition clientResponseContent = clientResponse.getContent();
            ClientResponseExceptionDefinition clientResponseException = clientResponse.getException();
            if (ObjectUtil.allNotNull(clientResponseException)) {
                if (!clientResponseException.getId().equals(clientRequestContent.getId())) {
                    throw new StatusRelationshipErrorException();
                }
            } else {
                if (!clientResponseContent.getId().equals(clientRequestContent.getId())) {
                    throw new StatusRelationshipErrorException();
                }
            }

            return clientResponse;
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
