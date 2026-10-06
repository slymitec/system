package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.ADefinitionObject;
import indi.sly.subsystem.periphery.proxies.lang.*;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.RemoteProcessorMediator;
import indi.sly.subsystem.periphery.proxies.values.RemoteDefinition;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RemoteObject extends ADefinitionObject<RemoteDefinition> {
    protected ProxyFactory factory;
    protected RemoteProcessorMediator processorMediator;
    private Class<? extends AProxyObject> proxyClass;

    public void setProxyClass(Class<? extends AProxyObject> proxyClass) {
        this.proxyClass = proxyClass;
    }

    public RemoteObject invoke(String method, Object... args) {
        if (ObjectUtil.isAnyNull(this.proxyClass) || ValueUtil.isAnyNullOrEmpty(method)) {
            throw new ConditionParametersException();
        }
        if (ObjectUtil.isAnyNull(args)) {
            args = new Object[0];
        }

        List<RemoteProcessorInvokeFunction> invokes = this.processorMediator.getInvokes();

        RemoteDefinition invokeRemote = null;

        for (RemoteProcessorInvokeFunction invoke : invokes) {
            invokeRemote = invoke.apply(invokeRemote, this.definition, this.proxyClass, method, args);
        }

        RemoteObject remote = this.factory.buildRemote(invokeRemote);

        return remote;
    }

    public void expire() {
        List<RemoteProcessorExpireConsumer> expires = this.processorMediator.getExpires();

        for (RemoteProcessorExpireConsumer expire : expires) {
            expire.accept(this.proxyClass, this.definition);
        }
    }

    public void die() {
        List<RemoteProcessorDieConsumer> dies = this.processorMediator.getDies();

        for (RemoteProcessorDieConsumer die : dies) {
            die.accept(this.proxyClass, this.definition);
        }
    }

    public String getValue() {
        return this.definition.getValue();
    }
}
