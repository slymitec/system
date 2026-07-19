package indi.sly.subsystem.periphery.proxies.prototypes.processors;

import indi.sly.subsystem.periphery.core.date.prototypes.DateTimeObject;
import indi.sly.subsystem.periphery.core.prototypes.processors.AResolver;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorDieConsumer;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorExpireConsumer;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorInvokeFunction;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorIsExpiredFunction;
import indi.sly.subsystem.periphery.proxies.prototypes.HandleTableObject;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.RemoteProcessorMediator;
import indi.sly.subsystem.periphery.proxies.values.RemoteDefinition;
import indi.sly.system.common.lang.StatusExpiredException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.DateTimeType;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RemoteCheckExpiredResolver extends AResolver implements IRemoteResolver {
    private final RemoteProcessorInvokeFunction invoke;
    private final RemoteProcessorIsExpiredFunction isExpired;
    private final RemoteProcessorExpireConsumer expire;
    private final RemoteProcessorDieConsumer die;

    public RemoteCheckExpiredResolver() {
        this.isExpired = (isExpired, remote, procedure) -> {
            DateTimeObject dateTime = this.coreManager.getDateTime();

            long expiredDate = remote.getDate().getOrDefault(DateTimeType.EXPIRED, 0L);

            return dateTime.getCurrent() > expiredDate;
        };

        this.die = (remote, procedure) -> {
            remote.setAlive(false);

            UUID handle = ObjectUtil.transferFromString(UUID.class, remote.getValue());

            HandleTableObject handleTable = procedure.getHandleTable();

            if (handleTable.isHandleExist(handle)) {
                handleTable.delete(handle);
            }
        };

        this.invoke = (invokeRemote, remote, procedure, method, parameters) -> {
            if (this.isExpired.apply(false, remote, procedure)) {
                this.die.accept(remote, procedure);

                throw new StatusExpiredException();
            }

            return invokeRemote;
        };


        this.expire = (remote, procedure, duration) -> {
            if (this.isExpired.apply(false, remote, procedure)) {
                this.die.accept(remote, procedure);

                throw new StatusExpiredException();
            }
        };
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public void resolve(RemoteDefinition remote, RemoteProcessorMediator processorMediator) {
        processorMediator.getInvokes().add(this.invoke);
        processorMediator.getIsExpires().add(this.isExpired);
        processorMediator.getExpires().add(this.expire);
        processorMediator.getDies().add(this.die);
    }
}
