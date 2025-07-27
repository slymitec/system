package indi.sly.system.services.faces;

import indi.sly.system.common.lang.StatusAlreadyFinishedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.kernel.core.FactoryManager;
import indi.sly.system.kernel.core.boot.prototypes.BootObject;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.files.FileSystemManager;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.services.core.environment.values.ServiceUserSpaceExtensionDefinition;
import indi.sly.system.services.jobs.JobService;
import indi.sly.system.services.jobs.values.UserContentResponseDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@RestController
public class StartUpController extends AController {
    @RequestMapping(value = {"/StartUp.action"}, method = {RequestMethod.GET})
    @Transactional
    public UserContentResponseDefinition startup(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init();

        if (ObjectUtil.isAnyNull(this.factoryManager)) {
            this.factoryManager = SpringHelper.getInstance(FactoryManager.class);

            this.factoryManager.startup(StartupType.STEP_INIT_SELF);
            this.factoryManager.startup(StartupType.STEP_AFTER_SELF);

            KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();
            KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

            UserSpaceDefinition userSpace = new UserSpaceDefinition();
            userSpace.setServiceSpace(new ServiceUserSpaceExtensionDefinition());
            this.factoryManager.setUserSpace(userSpace);
            this.factoryManager.getCoreObjectRepository().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);

            BootObject boot = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, BootObject.class);
            FileSystemManager fileSystemManager = this.factoryManager.getManager(FileSystemManager.class);
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
            UserManager userManager = this.factoryManager.getManager(UserManager.class);

            this.factoryManager.getCoreObjectRepository().addByClass(SpaceType.KERNEL, this.factoryManager.create(JobService.class));

            JobService jobService = this.factoryManager.getService(JobService.class);

            Long[] startups = new Long[]{
                    StartupType.STEP_INIT_SELF,
                    StartupType.STEP_AFTER_SELF,
                    StartupType.STEP_INIT_KERNEL,
                    StartupType.STEP_AFTER_KERNEL,
                    StartupType.STEP_INIT_SERVICE,
                    StartupType.STEP_AFTER_SERVICE
            };

            for (Long startup : startups) {
                memoryManager.startup(startup);
                boot.startup(startup);
                threadManager.startup(startup);
                processManager.startup(startup);
                typeManager.startup(startup);
                userManager.startup(startup);
                objectManager.startup(startup);
                fileSystemManager.startup(startup);

                jobService.startup(startup);
            }

            return new UserContentResponseDefinition();
        } else {
            UserContentResponseDefinition userContentResponseRaw = new UserContentResponseDefinition();

            userContentResponseRaw.getException().setClazz(StatusAlreadyFinishedException.class);
            userContentResponseRaw.getException().setOwner(StartUpController.class);
            userContentResponseRaw.getException().setMethod("startup");

            return userContentResponseRaw;
        }
    }
}
