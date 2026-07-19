package indi.sly.subsystem.periphery.proxies.prototypes.processors;

import indi.sly.subsystem.periphery.core.prototypes.processors.AResolver;
import indi.sly.subsystem.periphery.proxies.lang.*;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.RemoteProcessorMediator;
import indi.sly.subsystem.periphery.proxies.values.RemoteDefinition;
import indi.sly.subsystem.periphery.proxies.values.RemoteTypes;
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
    private final RemoteProcessorIsExpiredFunction isExpired;
    private final RemoteProcessorExpireConsumer expire;
    private final RemoteProcessorDieConsumer die;

    public RemoteCheckConditionResolver() {
        this.invoke = (invokeRemote, remote, procedure, method, parameters) -> {
            if (!remote.isAlive()) {
                throw new StatusRelationshipErrorException();
            }
            if (LogicalUtil.allNotEqual(remote.getType(), RemoteTypes.OBJECT, RemoteTypes.MANAGER)) {
                throw new StatusNotSupportedException();
            }

            return invokeRemote;
        };

        this.isExpired = (isExpired, remote, procedure) -> {
            if (!remote.isAlive()) {
                throw new StatusRelationshipErrorException();
            }
            if (LogicalUtil.allNotEqual(remote.getType(), RemoteTypes.OBJECT)) {
                throw new StatusNotSupportedException();
            }

            return isExpired;
        };

        this.expire = (remote, procedure, duration) -> {
            if (!remote.isAlive()) {
                throw new StatusRelationshipErrorException();
            }
            if (LogicalUtil.allNotEqual(remote.getType(), RemoteTypes.OBJECT)) {
                throw new StatusNotSupportedException();
            }
        };

        this.die = (remote, procedure) -> {
            if (!remote.isAlive()) {
                throw new StatusRelationshipErrorException();
            }
            if (LogicalUtil.allNotEqual(remote.getType(), RemoteTypes.OBJECT)) {
                throw new StatusNotSupportedException();
            }
        };
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public void resolve(RemoteDefinition remote, RemoteProcessorMediator processorMediator) {
        processorMediator.getInvokes().add(this.invoke);
        processorMediator.getIsExpires().add(this.isExpired);
        processorMediator.getExpires().add(this.expire);
        processorMediator.getDies().add(this.die);
    }
}
