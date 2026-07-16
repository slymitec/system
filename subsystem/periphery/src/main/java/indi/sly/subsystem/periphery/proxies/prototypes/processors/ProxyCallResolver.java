package indi.sly.subsystem.periphery.proxies.prototypes.processors;

import indi.sly.subsystem.periphery.calls.CallManager;
import indi.sly.subsystem.periphery.calls.prototypes.ConnectionObject;
import indi.sly.subsystem.periphery.calls.values.*;
import indi.sly.subsystem.periphery.core.prototypes.processors.AResolver;
import indi.sly.subsystem.periphery.proxies.lang.ProxyInvokeFunction;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.ProxyProcessorMediator;
import indi.sly.subsystem.periphery.proxies.values.ProxyCacheEntity;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProxyCallResolver extends AResolver implements IProxyResolver {
    private final ProxyInvokeFunction invoke;

    public ProxyCallResolver() {
        this.invoke = (clientResponse, proxy, method, parameters) -> {
            CallManager callManager = this.coreManager.getManager(CallManager.class);

            ClientRequestProcessIdRecord clientRequestProcessId = new ClientRequestProcessIdRecord(proxy.getContext().getProcessId(), proxy.getContext().getType(), proxy.getContext().getSecret(), proxy.getContext().getVerification());

            List<String> clientRequestContentParameters = new ArrayList<>();
            if (ObjectUtil.allNotNull(parameters)) {
                for (Object parameter : parameters) {
                    clientRequestContentParameters.add(ObjectUtil.transferToString(parameter));
                }
            }
            UserContentRequestRecord clientRequestContent = new UserContentRequestRecord(UUIDUtil.createRandom(), proxy.getTask(), method, clientRequestContentParameters);

            ClientRequestRecord clientRequest = new ClientRequestRecord(clientRequestProcessId, clientRequestContent);

            ConnectionObject connection = callManager.getConnection(proxy.getContext().getCall());
            clientResponse = connection.call(clientRequest);

            UserContentResponseRecord clientResponseContent = clientResponse.content();
            ClientResponseExceptionRecord clientResponseException = clientResponse.exception();
            if (ObjectUtil.allNotNull(clientResponseException)) {
                if (!clientResponseException.id().equals(clientRequestContent.id())) {
                    throw new StatusRelationshipErrorException();
                }
            } else {
                if (!clientResponseContent.id().equals(clientRequestContent.id())) {
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
