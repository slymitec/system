package indi.sly.subsystem.boot.prototypes;

import indi.sly.subsystem.periphery.core.CoreManager;
import indi.sly.subsystem.periphery.core.boot.prototypes.BootObject;
import indi.sly.subsystem.periphery.core.boot.prototypes.IStartupCapable;
import indi.sly.subsystem.periphery.core.boot.values.StartupType;
import indi.sly.subsystem.periphery.core.environment.values.SpaceType;
import indi.sly.subsystem.periphery.core.prototypes.AComponent;
import indi.sly.subsystem.periphery.memory.MemoryManager;
import indi.sly.subsystem.periphery.proxies.ProxyManager;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SystemStartUpComponent extends AComponent implements ApplicationRunner {
    @Override
    @Transactional
    public void run(@NonNull ApplicationArguments args) {
        if (ObjectUtil.isAnyNull(this.coreManager)) {
            this.coreManager = SpringHelper.getInstance(CoreManager.class);

            this.coreManager.startup(StartupType.STEP_INIT_SELF);
            this.coreManager.startup(StartupType.STEP_AFTER_SELF);

            List<IStartupCapable> startupCapableManagers = new ArrayList<>();
            startupCapableManagers.add(this.coreManager.getManager(MemoryManager.class));
            startupCapableManagers.add(this.coreManager.getObjectCollection().getByClass(SpaceType.PERIPHERY, BootObject.class));
            startupCapableManagers.add(this.coreManager.getManager(ProxyManager.class));

            Long[] startups = new Long[]{
                    StartupType.STEP_INIT_SELF,
                    StartupType.STEP_AFTER_SELF,
                    StartupType.STEP_INIT_PERIPHERY,
                    StartupType.STEP_AFTER_PERIPHERY
            };

            for (Long startup : startups) {
                for (IStartupCapable startupCapableManager : startupCapableManagers) {
                    startupCapableManager.startup(startup);
                }
            }
        }
    }
}
