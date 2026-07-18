package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.ADefinitionObject;
import indi.sly.subsystem.periphery.proxies.values.ProcedureDefinition;
import indi.sly.subsystem.periphery.proxies.values.ProcedureProcessRecord;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

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

    public <T extends AProxyObject> T getManager(Class<T> clazz) {
        return null;
    }
}
