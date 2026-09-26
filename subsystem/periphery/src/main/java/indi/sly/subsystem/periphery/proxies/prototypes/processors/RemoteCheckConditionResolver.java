package indi.sly.subsystem.periphery.proxies.prototypes.processors;

import indi.sly.subsystem.periphery.core.prototypes.processors.AResolver;
import indi.sly.subsystem.periphery.proxies.lang.*;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.RemoteProcessorMediator;
import indi.sly.subsystem.periphery.proxies.values.RemoteDefinition;
import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.LogicalUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RemoteCheckConditionResolver extends AResolver implements IRemoteResolver {
    private final RemoteProcessorInvokeFunction invoke;
    private final RemoteProcessorExpireConsumer expire;
    private final RemoteProcessorDieConsumer die;

    public RemoteCheckConditionResolver() {
        this.invoke = (invokeRemote, remote, method, parameters) -> {

            return invokeRemote;
        };

        this.expire = (remote, duration) -> {

        };

        this.die = (remote) -> {

        };
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public void resolve(RemoteDefinition remote, RemoteProcessorMediator processorMediator) {
        processorMediator.getInvokes().add(this.invoke);
        processorMediator.getExpires().add(this.expire);
        processorMediator.getDies().add(this.die);
    }
}
