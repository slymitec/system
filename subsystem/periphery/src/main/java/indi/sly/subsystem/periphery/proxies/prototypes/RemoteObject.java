package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AChildDefinitionObject;
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

import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RemoteObject extends ADefinitionObject<RemoteDefinition> {
    protected ProxyFactory factory;
    protected RemoteProcessorMediator processorMediator;

    public RemoteObject invoke(String method, Class<?> returnClazz, Object... args) {
        if (ValueUtil.isAnyNullOrEmpty(method)) {
            throw new ConditionParametersException();
        }
        if (ObjectUtil.isAnyNull(args)) {
            args = new Object[0];
        }

        List<RemoteProcessorInvokeFunction> invokes = this.processorMediator.getInvokes();

        RemoteDefinition invokeRemote = null;

        for (RemoteProcessorInvokeFunction invoke : invokes) {
            invokeRemote = invoke.apply(invokeRemote, this.definition, method, returnClazz, args);
        }

        RemoteObject remote = this.factory.buildRemote(invokeRemote);

        return remote;
    }

    public void expire(long duration) {
        List<RemoteProcessorExpireConsumer> expires = this.processorMediator.getExpires();

        for (RemoteProcessorExpireConsumer expire : expires) {
            expire.accept(this.definition, duration);
        }
    }

    public void die() {
        List<RemoteProcessorDieConsumer> dies = this.processorMediator.getDies();

        for (RemoteProcessorDieConsumer die : dies) {
            die.accept(this.definition);
        }
    }

    public String getValue() {
        return this.definition.getValue();
    }
}
