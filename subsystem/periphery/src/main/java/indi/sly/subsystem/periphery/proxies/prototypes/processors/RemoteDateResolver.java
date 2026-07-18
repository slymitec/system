package indi.sly.subsystem.periphery.proxies.prototypes.processors;

import indi.sly.subsystem.periphery.core.date.prototypes.DateTimeObject;
import indi.sly.subsystem.periphery.core.environment.values.CacheDurationType;
import indi.sly.subsystem.periphery.core.prototypes.processors.AResolver;
import indi.sly.subsystem.periphery.proxies.ProxyManager;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorExpireConsumer;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorInvokeFunction;
import indi.sly.subsystem.periphery.proxies.prototypes.HandleEntryObject;
import indi.sly.subsystem.periphery.proxies.prototypes.HandleTableObject;
import indi.sly.subsystem.periphery.proxies.prototypes.ProcedureObject;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.RemoteProcessorMediator;
import indi.sly.subsystem.periphery.proxies.values.RemoteDefinition;
import indi.sly.subsystem.periphery.proxies.values.RemoteTypes;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.DateTimeType;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RemoteDateResolver extends AResolver implements IRemoteResolver {
    private final RemoteProcessorInvokeFunction invoke;
    private final RemoteProcessorExpireConsumer expire;

    public RemoteDateResolver() {
        this.expire = (remote, procedure, duration) -> {
            DateTimeObject dateTime = this.coreManager.getDateTime();
            Instant instant = Instant.ofEpochMilli(dateTime.getCurrent());

            if (duration == CacheDurationType.INSTANT) {
                instant = instant.plus(Duration.ofSeconds(4L));
            } else if (duration == CacheDurationType.SHORT) {
                instant = instant.plus(Duration.ofSeconds(8L));
            } else if (duration == CacheDurationType.NORMAL) {
                instant = instant.plus(Duration.ofSeconds(16L));
            } else if (duration == CacheDurationType.LONG) {
                instant = instant.plus(Duration.ofSeconds(32L));
            } else if (duration == CacheDurationType.AGES) {
                instant = instant.plus(Duration.ofSeconds(64L));
            } else if (duration == CacheDurationType.PERMANENT) {
                instant = Instant.MAX;
            }

            remote.getDate().put(DateTimeType.EXPIRED, instant.toEpochMilli());
        };

        this.invoke = (invokeRemote, remote, procedure, method, parameters) -> {
            if (LogicalUtil.isAnyEqual(remote.getType(), RemoteTypes.OBJECT)) {
                this.expire.accept(remote, procedure, CacheDurationType.NORMAL);
            }

            DateTimeObject dateTime = this.coreManager.getDateTime();
            invokeRemote.getDate().put(DateTimeType.CREATE, dateTime.getCurrent());

            return invokeRemote;
        };

    }

    @Override
    public int order() {
        return 2;
    }

    @Override
    public void resolve(RemoteDefinition remote, RemoteProcessorMediator processorMediator) {
        processorMediator.getInvokes().add(invoke);
        processorMediator.getExpires().add(expire);
    }
}
