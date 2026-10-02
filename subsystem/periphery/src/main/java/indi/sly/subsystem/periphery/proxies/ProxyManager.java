package indi.sly.subsystem.periphery.proxies;

import indi.sly.subsystem.periphery.core.AManager;
import indi.sly.subsystem.periphery.core.boot.values.StartupType;
import indi.sly.subsystem.periphery.proxies.prototypes.ProxyFactory;
import indi.sly.subsystem.periphery.proxies.prototypes.SessionObject;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ValueUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

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

    public SessionObject getSession(UUID sessionId) {
        if (ValueUtil.isAnyNullOrEmpty(sessionId)) {
            throw new ConditionParametersException();
        }

        return this.factory.buildSession(sessionId);
    }
}
