package indi.sly.system.controllers.startup;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.controllers.AController;
import indi.sly.system.controllers.values.UserContentDefinition;
import indi.sly.system.controllers.values.UserContentResponseDefinition;
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
import indi.sly.system.services.auxiliary.AuxiliaryService;
import indi.sly.system.services.job.JobService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

@RestController
public class StartUpController extends AController {
    @RequestMapping(value = {"/StartUp.action"}, method = {RequestMethod.GET})
    @Transactional
    public UserContentResponseDefinition startup(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        UserContentDefinition userContent = this.init(request, response, session);

        if (ObjectUtil.isAnyNull(this.factoryManager)) {
            this.factoryManager = SpringHelper.createInstance(FactoryManager.class);

            this.factoryManager.startup(StartupType.STEP_INIT_SELF);
            this.factoryManager.startup(StartupType.STEP_AFTER_SELF);

            KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();
            KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

            this.factoryManager.setUserSpace(new UserSpaceDefinition());
            this.factoryManager.getCoreObjectRepository().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);

            BootObject boot = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, BootObject.class);
            FileSystemManager fileSystemManager = this.factoryManager.getManager(FileSystemManager.class);
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
            UserManager userManager = this.factoryManager.getManager(UserManager.class);

            this.factoryManager.getCoreObjectRepository().addByClass(SpaceType.KERNEL, this.factoryManager.create(AuxiliaryService.class));
            this.factoryManager.getCoreObjectRepository().addByClass(SpaceType.KERNEL, this.factoryManager.create(JobService.class));

            AuxiliaryService auxiliaryService = this.factoryManager.getService(AuxiliaryService.class);
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

                auxiliaryService.startup(startup);
                jobService.startup(startup);
            }
        }

        return userContent.getResponse();
    }
}
