package indi.sly.system.boot.prototypes;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.kernel.core.CoreManager;
import indi.sly.system.kernel.core.boot.prototypes.BootObject;
import indi.sly.system.kernel.core.boot.prototypes.IStartupCapable;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.environment.containers.KernelConfiguration;
import indi.sly.system.kernel.core.environment.containers.KernelSpace;
import indi.sly.system.kernel.core.environment.containers.UserSpace;
import indi.sly.system.kernel.core.environment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AComponent;
import indi.sly.system.kernel.files.FileSystemManager;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.services.ServiceManager;
import indi.sly.system.services.jobs.JobService;
import indi.sly.system.services.jobs.values.ClientResponseRecord;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SystemStartUpComponent extends AComponent implements ApplicationRunner {
    private void init() {
        KernelSpace kernelSpace = SpringHelper.getInstance(KernelSpace.class);

        this.coreManager = (CoreManager) kernelSpace.getClassedObjects().getOrDefault(CoreManager.class, null);

        if (ObjectUtil.allNotNull(this.coreManager)) {
            this.coreManager.check();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.init();

        if (ObjectUtil.isAnyNull(this.coreManager)) {
            this.coreManager = SpringHelper.getInstance(CoreManager.class);

            this.coreManager.startup(StartupType.STEP_INIT_SELF);
            this.coreManager.startup(StartupType.STEP_AFTER_SELF);

            KernelSpace kernelSpace = this.coreManager.getKernelSpace();
            KernelConfiguration kernelConfiguration = kernelSpace.getConfiguration();

            UserSpace userSpace = SpringHelper.getInstance(UserSpace.class);
            this.coreManager.setUserSpace(userSpace);

            List<IStartupCapable> startupCapableManagers = new ArrayList<>();
            startupCapableManagers.add(this.coreManager.getManager(MemoryManager.class));
            startupCapableManagers.add(this.coreManager.getObjectCollection().getByClass(SpaceType.KERNEL, BootObject.class));
            startupCapableManagers.add(this.coreManager.getManager(ThreadManager.class));
            startupCapableManagers.add(this.coreManager.getManager(ProcessManager.class));
            startupCapableManagers.add(this.coreManager.getManager(TypeManager.class));
            startupCapableManagers.add(this.coreManager.getManager(UserManager.class));
            startupCapableManagers.add(this.coreManager.getManager(ObjectManager.class));
            startupCapableManagers.add(this.coreManager.getManager(FileSystemManager.class));
            startupCapableManagers.add(this.coreManager.getManager(ServiceManager.class));

            this.coreManager.getObjectCollection().addByClass(SpaceType.KERNEL, this.coreManager.create(JobService.class));
            startupCapableManagers.add(this.coreManager.getService(JobService.class));

            Long[] startups = new Long[]{
                    StartupType.STEP_INIT_SELF,
                    StartupType.STEP_AFTER_SELF,
                    StartupType.STEP_INIT_KERNEL,
                    StartupType.STEP_AFTER_KERNEL,
                    StartupType.STEP_INIT_SERVICE,
                    StartupType.STEP_AFTER_SERVICE
            };

            for (Long startup : startups) {
                for (IStartupCapable startupCapableManager : startupCapableManagers) {
                    startupCapableManager.startup(startup);
                }
            }

            this.coreManager.setUserSpace(null);

        }
    }
}
