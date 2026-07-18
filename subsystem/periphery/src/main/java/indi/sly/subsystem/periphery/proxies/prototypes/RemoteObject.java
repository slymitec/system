package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.date.prototypes.DateTimeObject;
import indi.sly.subsystem.periphery.core.environment.values.CacheDurationType;
import indi.sly.subsystem.periphery.core.prototypes.AChildDefinitionObject;
import indi.sly.subsystem.periphery.core.prototypes.ADefinitionObject;
import indi.sly.subsystem.periphery.proxies.ProxyManager;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorExpireConsumer;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorInvokeFunction;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorIsExpiredFunction;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.RemoteProcessorMediator;
import indi.sly.subsystem.periphery.proxies.values.RemoteDefinition;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.DateTimeType;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RemoteObject extends AChildDefinitionObject<RemoteDefinition, ProcedureObject> {
    protected ProxyFactory factory;
    protected RemoteProcessorMediator processorMediator;

    public long getType() {
        return this.definition.getType();
    }

    public String getRemoteClazz() {
        return this.definition.getClazz();
    }

    public String getRemoteValue() {
        return this.definition.getValue();
    }

    public RemoteObject invoke(String method, String returnClazz, Object... args) {
        if (ValueUtil.isAnyNullOrEmpty(method) || ObjectUtil.isAnyNull(returnClazz)) {
            throw new ConditionParametersException();
        }
        if (ObjectUtil.isAnyNull(args)) {
            args = new Object[0];
        }

        List<RemoteProcessorInvokeFunction> invokes = this.processorMediator.getInvokes();

        RemoteDefinition invokeRemote = null;

        for (RemoteProcessorInvokeFunction invoke : invokes) {
            invokeRemote = invoke.apply(invokeRemote, this.definition, this.base, method, args);
        }

        return this.factory.build(invokeRemote, this.base);
    }

    public boolean isExpired() {
        List<RemoteProcessorIsExpiredFunction> isExpireds = this.processorMediator.getIsExpireds();

        boolean result = false;

        for (RemoteProcessorIsExpiredFunction isExpired : isExpireds) {
            result = isExpired.apply(result, this.definition, this.base);
        }

        return result;
    }

    public void expire(long duration) {
        List<RemoteProcessorExpireConsumer> expires = this.processorMediator.getExpires();

        for (RemoteProcessorExpireConsumer expire : expires) {
            expire.accept(this.definition, this.base, duration);
        }
    }

    public ProcedureObject getProcedure() {
        return this.base;
    }

    public AProxyObject getProxy() {
        ProxyManager proxyManager = this.coreManager.getManager(ProxyManager.class);


        return null;
    }
}
