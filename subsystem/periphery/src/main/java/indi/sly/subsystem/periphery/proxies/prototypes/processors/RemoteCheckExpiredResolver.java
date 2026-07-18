package indi.sly.subsystem.periphery.proxies.prototypes.processors;

import indi.sly.subsystem.periphery.core.date.prototypes.DateTimeObject;
import indi.sly.subsystem.periphery.core.prototypes.processors.AResolver;
import indi.sly.subsystem.periphery.proxies.ProxyManager;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorExpireConsumer;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorInvokeFunction;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorIsExpiredFunction;
import indi.sly.subsystem.periphery.proxies.prototypes.HandleEntryObject;
import indi.sly.subsystem.periphery.proxies.prototypes.ProcedureObject;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.RemoteProcessorMediator;
import indi.sly.subsystem.periphery.proxies.values.RemoteDefinition;
import indi.sly.subsystem.periphery.proxies.values.RemoteTypes;
import indi.sly.system.common.lang.Consumer1;
import indi.sly.system.common.lang.StatusExpiredException;
import indi.sly.system.common.supports.LogicalUtil;
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

    public RemoteCheckExpiredResolver() {
        Consumer1<RemoteDefinition> checkRemoteExpired = remote -> {
            if (LogicalUtil.isAnyEqual(remote.getType(), RemoteTypes.OBJECT)) {
                DateTimeObject dateTime = this.coreManager.getDateTime();
                long expiredDate = remote.getDate().getOrDefault(DateTimeType.EXPIRED, 0L);

                if (dateTime.getCurrent() > expiredDate) {
                    throw new StatusExpiredException();
                }
            }
        };

        this.invoke = (invokeRemote, remote, procedure, method, parameters) -> {
            checkRemoteExpired.accept(remote);

            return invokeRemote;
        };

        this.isExpired = (isExpired, remote, procedure) -> {
            DateTimeObject dateTime = this.coreManager.getDateTime();

            long expiredDate = remote.getDate().getOrDefault(DateTimeType.EXPIRED, 0L);

            return dateTime.getCurrent() > expiredDate;
        };

        this.expire = (remote, procedure, duration) -> {
            checkRemoteExpired.accept(remote);
        };
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public void resolve(RemoteDefinition remote, RemoteProcessorMediator processorMediator) {
        processorMediator.getInvokes().add(invoke);
        processorMediator.getIsExpireds().add(isExpired);
        processorMediator.getExpires().add(expire);
    }
}
