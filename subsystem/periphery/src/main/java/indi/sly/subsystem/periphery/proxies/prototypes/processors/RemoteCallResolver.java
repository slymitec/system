package indi.sly.subsystem.periphery.proxies.prototypes.processors;

import indi.sly.subsystem.periphery.core.prototypes.processors.AResolver;
import indi.sly.subsystem.periphery.proxies.ProxyManager;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorDieConsumer;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorExpireConsumer;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorInvokeFunction;
import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.IKernelObjectActor;
import indi.sly.subsystem.periphery.proxies.prototypes.ProxyFactory;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.RemoteProcessorMediator;
import indi.sly.subsystem.periphery.proxies.values.*;
import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.supports.*;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RemoteCallResolver extends AResolver implements IRemoteResolver {
    private final RemoteProcessorInvokeFunction invoke;
    private final RemoteProcessorExpireConsumer expire;
    private final RemoteProcessorDieConsumer die;

    @SuppressWarnings("unchecked")
    public RemoteCallResolver() {
        this.invoke = (invokeRemote, remote, method, parameters) -> {
            ProxyManager proxyManager = this.coreManager.getManager(ProxyManager.class);
            ProxyFactory proxyFactory = proxyManager.getFactory();

            CallContextRecord callContext = remote.getCallContext();

            List<String> clientRequestContentParameters = new ArrayList<>();
            if (ObjectUtil.allNotNull(parameters)) {
                for (Object parameter : parameters) {
                    clientRequestContentParameters.add(ObjectUtil.transferToString(parameter));
                }
            }

            ClientRequestRecord clientRequest = new ClientRequestRecord(method, clientRequestContentParameters);
            CallRequestRecord callRequest = new CallRequestRecord(callContext, clientRequest);

            IKernelObjectActor kernelObjectActor = proxyFactory.getKernelObjectActor(remote.getTaskName(), ObjectUtil.transferFromString(UUID.class, remote.getValue()));

            ClientResponseRecord clientResponse = kernelObjectActor.call(callRequest).block();

            if (ObjectUtil.isAnyNull(clientResponse)) {
                throw new StatusUnexpectedException();
            }

            switch (clientResponse.type()) {
                case ClientResponseTypes.NORMAL -> {
                    invokeRemote = new RemoteDefinition();

                    invokeRemote.setCallContext(callContext);
                    invokeRemote.setValue(clientResponse.value());
                }
                case ClientResponseTypes.SYSTEM_EXCEPTION -> {
                    String clientResponseValue = ObjectUtil.transferFromString(String.class, clientResponse.value());

                    if (ValueUtil.isAnyNullOrEmpty(clientResponseValue)) {
                        throw new StatusUnexpectedException();
                    }

                    throw proxyFactory.getSystemException(clientResponseValue);
                }
                case ClientResponseTypes.OTHER_EXCEPTION -> {
                    String clientResponseValue = ObjectUtil.transferFromString(String.class, clientResponse.value());

                    if (ValueUtil.isAnyNullOrEmpty(clientResponseValue)) {
                        throw new StatusUnexpectedException();
                    }

                    throw new RuntimeException(clientResponseValue);
                }
            }

            return invokeRemote;
        };

        this.expire = (remote) -> {
            this.invoke.apply(null, remote, "cache", new Object[0]);
        };

        this.die = (remote) -> {
            this.invoke.apply(null, remote, "uncache", new Object[0]);
        };
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public void resolve(RemoteDefinition remote, RemoteProcessorMediator processorMediator) {
        if (!ValueUtil.isAnyNullOrEmpty(remote.getValue())) {
            processorMediator.getInvokes().add(this.invoke);
            processorMediator.getExpires().add(this.expire);
            processorMediator.getDies().add(this.die);
        }
    }
}
