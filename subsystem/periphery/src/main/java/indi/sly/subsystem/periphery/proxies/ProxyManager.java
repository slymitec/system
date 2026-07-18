package indi.sly.subsystem.periphery.proxies;

import indi.sly.subsystem.periphery.calls.values.ClientRequestProcessIdRecord;
import indi.sly.subsystem.periphery.core.AManager;
import indi.sly.subsystem.periphery.core.boot.values.StartupType;
import indi.sly.subsystem.periphery.proxies.prototypes.ProcedureObject;
import indi.sly.subsystem.periphery.proxies.prototypes.ProxyFactory;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProxyManager extends AManager {
    private ProxyFactory factory;

    public ProxyFactory getFactory() {
        return this.factory;
    }

    @Override
    public void startup(long startup) {
        if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_SELF)) {
            this.factory = this.coreManager.create(ProxyFactory.class);
            this.factory.init();
        } else if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_PERIPHERY)) {
        }
    }

    @Override
    public void shutdown() {
    }

    public ProcedureObject createProxyContext(String call, long type, ClientRequestProcessIdRecord clientRequestProcessId) {
        if (ValueUtil.isAnyNullOrEmpty(call) || ObjectUtil.isAnyNull(clientRequestProcessId)) {
            throw new ConditionParametersException();
        }

        return null;
    }
}
