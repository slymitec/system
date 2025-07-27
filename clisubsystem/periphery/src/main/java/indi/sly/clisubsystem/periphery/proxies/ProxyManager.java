package indi.sly.clisubsystem.periphery.proxies;

import indi.sly.clisubsystem.periphery.core.AManager;
import indi.sly.clisubsystem.periphery.core.boot.values.StartupType;
import indi.sly.system.common.supports.LogicalUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProxyManager extends AManager {
    @Override
    public void startup(long startup) {
        if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_SELF)) {

        } else if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_PERIPHERY)) {
        }
    }

    @Override
    public void shutdown() {
    }


}
