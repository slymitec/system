package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.ADefinitionObject;
import indi.sly.subsystem.periphery.proxies.values.ProcedureDefinition;
import indi.sly.subsystem.periphery.proxies.values.ProcedureProcessRecord;
import indi.sly.subsystem.periphery.proxies.values.RemoteDefinition;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcedureObject extends ADefinitionObject<ProcedureDefinition> {
    protected ProxyFactory factory;

    public String getCall() {
        return this.definition.getCall();
    }

    public ProcedureProcessRecord getProcess() {
        return this.definition.getProcess();
    }

    public HandleTableObject getHandleTable() {
        return this.factory.buildHandleTable(this.definition.getHandleTable(), this);
    }

    @SuppressWarnings("unchecked")
    public <T extends AProxyObject> T getManager(Class<T> clazz) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        RemoteDefinition remote = this.factory.getCachedProxyManagers().getOrDefault(clazz, null);
        
        if (ObjectUtil.isAnyNull(remote)) {
            throw new StatusNotExistedException();
        }

        return (T) this.factory.buildProxy(clazz, this.factory.buildRemote(remote, this));
    }
}
