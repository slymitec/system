package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AChildDefinitionObject;
import indi.sly.subsystem.periphery.proxies.ProxyManager;
import indi.sly.subsystem.periphery.proxies.lang.*;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.RemoteProcessorMediator;
import indi.sly.subsystem.periphery.proxies.values.RemoteDefinition;
import indi.sly.subsystem.periphery.proxies.values.RemoteTypes;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RemoteObject extends AChildDefinitionObject<RemoteDefinition, ProcedureObject> {
    protected ProxyFactory factory;
    protected RemoteProcessorMediator processorMediator;

    public long getType() {
        if (!this.definition.isAlive()) {
            throw new StatusRelationshipErrorException();
        }

        return this.definition.getType();
    }

    public String getRemoteClazz() {
        if (!this.definition.isAlive()) {
            throw new StatusRelationshipErrorException();
        }

        return this.definition.getClazz();
    }

    public String getRemoteValue() {
        if (!this.definition.isAlive()) {
            throw new StatusRelationshipErrorException();
        }

        return this.definition.getValue();
    }

    public RemoteObject invoke(String method, Object... args) {
        if (ValueUtil.isAnyNullOrEmpty(method)) {
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

        RemoteObject remote = this.factory.buildRemote(invokeRemote, this.base);

        if (LogicalUtil.isAnyEqual(remote.getType(), RemoteTypes.OBJECT)) {
            HandleTableObject handleTable = this.base.getHandleTable();
            handleTable.add(remote);
        }

        return remote;
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

    public void die() {
        List<RemoteProcessorDieConsumer> dies = this.processorMediator.getDies();

        for (RemoteProcessorDieConsumer die : dies) {
            die.accept(this.definition, this.base);
        }
    }
}
